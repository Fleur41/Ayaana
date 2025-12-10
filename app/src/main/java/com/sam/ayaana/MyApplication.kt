package com.sam.ayaana

import android.app.Application
import com.google.ai.client.generativeai.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("Ayaana Application created")

    }
}