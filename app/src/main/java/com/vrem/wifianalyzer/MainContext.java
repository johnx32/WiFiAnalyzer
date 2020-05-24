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

package com.vrem.wifianalyzer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;

import com.vrem.wifianalyzer.settings.Repository;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.vendor.model.VendorService;
import com.vrem.wifianalyzer.wifi.filter.adapter.FiltersAdapter;
import com.vrem.wifianalyzer.wifi.scanner.ScannerService;
import com.vrem.wifianalyzer.wifi.scanner.ScannerServiceFactory;

import androidx.annotation.NonNull;

public enum MainContext {
    INSTANCE;

    private Settings settings;
    private MainActivity mainActivity;
    private ScannerService scannerService;
    private VendorService vendorService;
    private Configuration configuration;
    private FiltersAdapter filtersAdapter;

    public Settings getSettings() {
        return settings;
    }

    void setSettings(Settings settings) {
        this.settings = settings;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public ScannerService getScannerService() {
        return scannerService;
    }

    void setScannerService(ScannerService scannerService) {
        this.scannerService = scannerService;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Context getContext() {
        return mainActivity.getApplicationContext();
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    public LayoutInflater getLayoutInflater() {
        return mainActivity.getLayoutInflater();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public FiltersAdapter getFiltersAdapter() {
        return filtersAdapter;
    }

    void setFiltersAdapter(FiltersAdapter filtersAdapter) {
        this.filtersAdapter = filtersAdapter;
    }

    void initialize(@NonNull MainActivity mainActivity, boolean largeScreen) {
        Context applicationContext = mainActivity.getApplicationContext();
        Handler handler = new Handler();
        Repository repository = new Repository(applicationContext);
        Settings currentSettings = new Settings(repository);
        Configuration currentConfiguration = new Configuration(largeScreen);

        setMainActivity(mainActivity);
        setConfiguration(currentConfiguration);
        setSettings(currentSettings);
        setVendorService(new VendorService(mainActivity.getResources()));
        setScannerService(ScannerServiceFactory.makeScannerService(mainActivity, handler, currentSettings));
        setFiltersAdapter(new FiltersAdapter(currentSettings));
    }

}
