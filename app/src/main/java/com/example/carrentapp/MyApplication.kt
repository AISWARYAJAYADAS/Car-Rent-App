package com.example.carrentapp

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Google Places API with your API key
        Places.initialize(applicationContext, "API KEY")
    }
}
