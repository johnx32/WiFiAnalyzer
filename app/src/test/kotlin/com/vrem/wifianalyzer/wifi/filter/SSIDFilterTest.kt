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
package com.vrem.wifianalyzer.wifi.filter

import android.app.Dialog
import android.os.Build
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.whenever
import com.vrem.wifianalyzer.R
import com.vrem.wifianalyzer.RobolectricUtil
import com.vrem.wifianalyzer.wifi.filter.SSIDFilter.OnChange
import com.vrem.wifianalyzer.wifi.filter.adapter.SSIDAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@LooperMode(LooperMode.Mode.PAUSED)
class SSIDFilterTest {
    private val dialog = mock(Dialog::class.java)
    private val editText = mock(EditText::class.java)
    private val view = mock(View::class.java)
    private val ssidAdapter = mock(SSIDAdapter::class.java)
    private val editable = mock(Editable::class.java)

    @Before
    fun setUp() {
        RobolectricUtil.INSTANCE.activity
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(dialog)
        verifyNoMoreInteractions(editText)
        verifyNoMoreInteractions(view)
        verifyNoMoreInteractions(ssidAdapter)
        verifyNoMoreInteractions(editable)
    }

    @Test
    fun testSSIDFilterWithValues() {
        // setup
        val values: Set<String> = setOf("", " ", "ABC", " JDS ")
        whenever(ssidAdapter.selections).thenReturn(values)
        whenever(dialog.findViewById<EditText>(R.id.filterSSIDtext)).thenReturn(editText)
        whenever(dialog.findViewById<View>(R.id.filterSSID)).thenReturn(view)
        val expected = "ABC JDS"
        // execute
        SSIDFilter(ssidAdapter, dialog)
        // verify
        verify(ssidAdapter).selections
        verify(editText).setText(expected)
        verify(dialog).findViewById<EditText>(R.id.filterSSIDtext)
        verify(dialog).findViewById<View>(R.id.filterSSID)
        verify(view).visibility = View.VISIBLE
        verify(editText).addTextChangedListener(ArgumentMatchers.any(OnChange::class.java))
    }

    @Test
    fun testOnChangeAfterTextChangedWithValues() {
        // setup
        val value = " ABS ADF "
        val onChange = OnChange(ssidAdapter)
        whenever(editable.toString()).thenReturn(value)
        val expected: Set<String> = setOf("ABS", "ADF")
        // execute
        onChange.afterTextChanged(editable)
        // verify
        verify(ssidAdapter).selections = expected
    }
}