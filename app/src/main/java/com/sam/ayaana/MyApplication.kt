package com.sam.ayaana

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

//        if (BuildConfig.DEBUG){
//            Timber.plant(Timber.DebugTree())
//        }

        Log.d("MyApplication", "Ayaana Application created")

    }
}