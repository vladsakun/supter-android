package com.supter.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.supter.SupterApplication

class SystemUtils {

    companion object {

        const val bearer = "Bearer "

        fun isNightMode(context: Context): Boolean {
            val currentNightMode =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            return when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    false
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    true
                }
                else -> {
                    true
                }
            }
        }

        fun saveToken(applicationContext: Context, token: String) {

            val sharedPreferences =
                applicationContext.getSharedPreferences(TOKEN_SPRF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(TOKEN_NAME, token).apply()

        }

        fun getToken(applicationContext: Context): String {
            val sharedPreferences =
                applicationContext.getSharedPreferences(TOKEN_SPRF_NAME, Context.MODE_PRIVATE)
            return bearer + sharedPreferences.getString(TOKEN_NAME, "")!!
        }

        fun deleteToken(applicationContext: Context) {
            val sharedPreferences =
                applicationContext.getSharedPreferences(TOKEN_SPRF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().remove(TOKEN_NAME).apply()
        }
    }
}