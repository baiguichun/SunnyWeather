package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val TOKEN = "dG4nZGvVr7b1NgDd"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}