package com.supter.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.supter.SupterApplication

class SystemUtils {

    companion object {

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

        fun saveToken(applicationContext:Context, token:String){

            val sharedPreferences = applicationContext.getSharedPreferences(TOKEN_SPRF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(TOKEN_NAME, token)
            editor.apply()

        }
    }
}