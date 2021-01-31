package com.supter.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SystemUtils {

    companion object {

        const val bearer = "Bearer "

        fun isNightMode(context: Context): Boolean {
            val currentNightMode =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            return when (currentNightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> {
                    false
                }
                AppCompatDelegate.MODE_NIGHT_YES -> {
                    true
                }
                else -> {
                    true
                }
            }
        }

        fun setColorMode(applicationContext: Context, colorMode: Int) {

            val sharedPreferences =
                applicationContext.getSharedPreferences(THEME_SPRF_NAME, Context.MODE_PRIVATE)

            sharedPreferences.edit {
                putInt(COLOR_MODE, colorMode)
            }

            AppCompatDelegate.setDefaultNightMode(colorMode)

        }

        fun getColorMode(applicationContext: Context): Int {
//            val sharedPreferences =
//                applicationContext.getSharedPreferences(THEME_SPRF_NAME, Context.MODE_PRIVATE)
//            return sharedPreferences.getInt(COLOR_MODE, AppCompatDelegate.MODE_NIGHT_YES)
            return AppCompatDelegate.MODE_NIGHT_YES
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

        fun hideKeyboard(activity: Activity) {
            val view:View? = activity.findViewById(android.R.id.content)
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        fun View.showKeyboard(){
            if(this.requestFocus()){
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

                // here is one more tricky issue
                // imm.showSoftInputMethod doesn't work well
                // and imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0) doesn't work well for all cases too
                imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            }
        }
    }
}