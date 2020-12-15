package com.supter

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.supter.utils.SystemUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SupterApplication : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(SystemUtils.getColorMode(applicationContext))
        super.onCreate()
    }
}