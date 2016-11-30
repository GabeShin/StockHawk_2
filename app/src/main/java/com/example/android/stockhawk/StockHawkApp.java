package com.example.android.stockhawk;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Gabe on 2016-11-24.
 */
public class StockHawkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
