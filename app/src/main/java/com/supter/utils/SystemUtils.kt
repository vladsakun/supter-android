package com.supter.utils

import android.content.Context
import android.content.res.Configuration

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
    }
}