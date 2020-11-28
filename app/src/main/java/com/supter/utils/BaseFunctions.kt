package com.supter.utils

import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.PurchaseListResponse
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


private val TAG = "BaseFunctions"

fun isOnline(context: Context): Boolean {
    var isOnline = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    isOnline = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return isOnline
}

fun getAttrColor(attrId: Int, applicationContext: Context): Int{
    val typedValue = TypedValue()
    val theme: Theme = applicationContext.theme
    theme.resolveAttribute(attrId, typedValue, true)
    @ColorInt val color = typedValue.data
    return color
}