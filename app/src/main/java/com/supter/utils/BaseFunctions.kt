package com.supter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.supter.BuildConfig
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.Data
import com.supter.data.response.DataItem
import java.util.*

private val TAG = "Supter"

fun isOnline(context: Context): Boolean {
    val isOnline: Boolean
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

fun logException(e: Exception) {
    val stackTraceElement = Thread.currentThread().stackTrace[3]

    val fullClassName = stackTraceElement.className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)

    val message = """
     Data: ${Date()}
     File name: ${stackTraceElement.fileName}
     Class name: $className
     Method Name: ${stackTraceElement.methodName}
     Line Number: ${stackTraceElement.lineNumber}
     =======
         """.trimIndent()

    if (BuildConfig.DEBUG) {
        Log.e(TAG, message, e)
    }

}

fun convertDataItemListToPurchaseEntityList(dataItemList: List<DataItem>): List<PurchaseEntity> {
    val resultList = ArrayList<PurchaseEntity>()

    for (dataItem in dataItemList) {
        resultList.add(convertDataItemToPurchaseEntity(dataItem))
    }

    return resultList
}

fun convertDataItemToPurchaseEntity(dataItem: DataItem): PurchaseEntity {
    with(dataItem) {
        return PurchaseEntity(
            id,
            title,
            price.toDouble(),
            order,
            stage,
            potential,
            description,
            null,
            remind = 0.0,
            realPeriod = 0,
            null
        )
    }
}

