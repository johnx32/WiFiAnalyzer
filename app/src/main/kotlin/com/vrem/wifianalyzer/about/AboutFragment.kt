/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2020 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.vrem.wifianalyzer.about

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.vrem.util.EMPTY
import com.vrem.util.buildMinVersionP
import com.vrem.util.readFile
import com.vrem.wifianalyzer.MainContext.INSTANCE
import com.vrem.wifianalyzer.R
import com.vrem.wifianalyzer.databinding.AboutContentBinding
import java.text.SimpleDateFormat
import java.util.*


class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: AboutContentBinding = AboutContentBinding.inflate(inflater, container, false)
        val activity: FragmentActivity = requireActivity()
        setTexts(binding, activity)
        setOnClicks(binding, activity)
        return binding.root
    }

    private fun setTexts(binding: AboutContentBinding, activity: FragmentActivity) {
        binding.aboutCopyright.text = copyright()
        binding.aboutVersionInfo.text = version(activity)
        binding.aboutPackageName.text = activity.packageName
    }

    private fun setOnClicks(binding: AboutContentBinding, activity: FragmentActivity) {
        val gpl = AlertDialogClickListener(activity, R.string.gpl, R.raw.gpl)
        binding.license.setOnClickListener(gpl)
        val contributors = AlertDialogClickListener(activity, R.string.about_contributor_title, R.raw.contributors, false)
        binding.contributors.setOnClickListener(contributors)
        val al = AlertDialogClickListener(activity, R.string.al, R.raw.al)
        binding.apacheCommonsLicense.setOnClickListener(al)
        binding.graphViewLicense.setOnClickListener(al)
        binding.materialDesignIconsLicense.setOnClickListener(al)
        binding.writeReview.setOnClickListener(WriteReviewClickListener(activity))
    }

    private fun copyright(): String =
            resources.getString(R.string.app_copyright) + SimpleDateFormat(YEAR_FORMAT, Locale.getDefault()).format(Date())

    private fun version(activity: FragmentActivity): String {
        val configuration = INSTANCE.configuration
        return applicationVersion(activity) +
                configuration.sizeAvailable.let { "S" } +
                configuration.largeScreen.let { "L" } +
                " (" + Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT + ")"
    }

    private fun applicationVersion(activity: FragmentActivity): String =
            try {
                val packageInfo: PackageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
                packageInfo.versionName + " - " + versionCode(packageInfo)
            } catch (e: NameNotFoundException) {
                String.EMPTY
            }

    private fun versionCode(packageInfo: PackageInfo): String =
            if (buildMinVersionP()) {
                packageInfo.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toString()
            }

    private class WriteReviewClickListener(private val activity: Activity) : View.OnClickListener {
        override fun onClick(view: View) {
            val url = "market://details?id=" + activity.applicationContext.packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(view.context, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    private class AlertDialogClickListener(private val activity: Activity,
                                           private val titleId: Int,
                                           private val resourceId: Int,
                                           private val isSmallFont: Boolean = true) : View.OnClickListener {
        override fun onClick(view: View) {
            if (!activity.isFinishing) {
                val text = readFile(activity.resources, resourceId)
                val alertDialog: AlertDialog = AlertDialog.Builder(view.context)
                        .setTitle(titleId)
                        .setMessage(text)
                        .setNeutralButton(android.R.string.ok, Close())
                        .create()
                alertDialog.show()
                if (isSmallFont) {
                    alertDialog.findViewById<TextView>(android.R.id.message).textSize = 8f
                }
            }
        }

        private class Close : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
            }
        }

    }

    companion object {
        private const val YEAR_FORMAT = "yyyy"
    }
}