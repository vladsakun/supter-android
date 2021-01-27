package com.supter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.supter.utils.SystemUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SupterApplication : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(SystemUtils.getColorMode(applicationContext))
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        const val CHANNEL_NAME = "Income reminder"
        const val CHANNEL_DESCRIPTION = "Add income reminder to your balance"
        const val CHANNEL_ID = "income_reminder"
    }
}