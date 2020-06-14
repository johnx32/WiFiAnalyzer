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
package com.vrem.wifianalyzer.wifi.scanner

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import com.vrem.annotation.OpenClass
import com.vrem.wifianalyzer.MainContext

internal class CacheResult(val scanResult: ScanResult, val average: Int)

internal data class CacheKey(val bssid: String, val ssid: String)

@OpenClass
@OptIn(ExperimentalStdlibApi::class)
internal class Cache {
    private val scanResults: ArrayDeque<List<ScanResult>> = ArrayDeque(MAXIMUM)
    private var wifiInfo: WifiInfo? = null
    private var count: Int = MINIMUM

    fun scanResults(): List<CacheResult> =
            combineCache()
                    .groupingBy { CacheKey(it.BSSID, it.SSID) }
                    .aggregate { _, accumulator: CacheResult?, element, first ->
                        CacheResult(element, calculate(first, element, accumulator))
                    }
                    .values
                    .toList()

    fun add(scanResults: List<ScanResult>, wifiInfo: WifiInfo?) {
        count = if (count > MAXIMUM * 2) MINIMUM else count + 1
        while (this.scanResults.size >= size()) {
            this.scanResults.removeLastOrNull()
        }
        this.scanResults.addFirst(scanResults)
        this.wifiInfo = wifiInfo
    }

    fun first(): List<ScanResult> = scanResults.first()

    fun last(): List<ScanResult> = scanResults.last()

    fun size(): Int =
            if (sizeAvailable)
                with(MainContext.INSTANCE.settings.scanSpeed()) {
                    when {
                        this < 2 -> MAXIMUM
                        this < 5 -> MAXIMUM - 1
                        this < 10 -> MAXIMUM - 2
                        else -> MINIMUM
                    }
                }
            else MINIMUM

    fun wifiInfo(): WifiInfo? = wifiInfo

    private fun calculate(first: Boolean, element: ScanResult, accumulator: CacheResult?): Int {
        val average: Int = if (first) element.level else (accumulator!!.average + element.level) / 2
        return (if (sizeAvailable) average else average - SIZE * count / 2).coerceIn(LEVEL_MINIMUM, LEVEL_MAXIMUM)
    }

    private fun combineCache(): List<ScanResult> =
            scanResults.flatten().sortedWith(comparator())

    private fun comparator(): Comparator<ScanResult> =
            compareBy<ScanResult> { it.BSSID }.thenBy { it.SSID }.thenBy { it.level }

    private val sizeAvailable: Boolean
        get() = MainContext.INSTANCE.configuration.sizeAvailable

    companion object {
        private const val MINIMUM: Int = 1
        private const val MAXIMUM: Int = 4
        private const val SIZE: Int = MINIMUM + MAXIMUM
        private const val LEVEL_MINIMUM: Int = -100
        private const val LEVEL_MAXIMUM: Int = 0
    }
}