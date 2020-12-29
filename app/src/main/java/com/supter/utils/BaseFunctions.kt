package com.supter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.supter.BuildConfig
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.Data
import com.supter.data.response.DataItem
import com.supter.data.response.PurchaseData
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.ceil
import kotlin.math.round

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

fun convertDataItemListToPurchaseEntityList(dataItemList: List<PurchaseData>): List<PurchaseEntity> {
    val resultList = ArrayList<PurchaseEntity>()

    for (dataItem in dataItemList) {
        Log.d(TAG, "$dataItem")
        resultList.add(convertDataItemToPurchaseEntity(dataItem))
    }

    return resultList
}

fun convertDataItemToPurchaseEntity(dataItem: PurchaseData): PurchaseEntity {
    with(dataItem) {
        return PurchaseEntity(
            id,
            title,
            price,
            order,
            stage,
            potential,
            description,
            null,
            remind = 0.0,
            realPeriod = 0,
            thinkingTime,
            createdAt,
            null
        )
    }
}

fun getPrettyDate(date: Double): String {
    val time = date * 24 // hours

    return if (time >= 24.0 && time < (31.0 * 24)) {
        (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
    } else if (time >= (31.0 * 24) && time < (365 * 24)) {
        (BigDecimal(time / (31 * 24)).setScale(
            1,
            RoundingMode.HALF_EVEN
        )).toString() + " months"
    } else if (time >= (365 * 24)) {
        (BigDecimal(time / (365 * 24)).setScale(
            1,
            RoundingMode.HALF_EVEN
        )).toString() + " years"
    } else {
        (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
    }
}

fun updatePurchasesData(purchaseList: List<PurchaseEntity>, user: UserEntity): List<PurchaseEntity> {

    if (user.incomeRemainder != null && user.period != null) {

        val newPurchaseList = mutableListOf<PurchaseEntity>()
        val purchaseListWithoutDoneAndSortedByStage = mutableListOf<PurchaseEntity>()

        val processList =
            purchaseList.filter { it.stage == STATUS_PROCESS }.sortedBy { it.order }

        for (purchase in processList) {
            purchaseListWithoutDoneAndSortedByStage.add(purchase)
        }

        val statusWant =
            purchaseList.filter { it.stage == STATUS_WANT }.sortedBy { it.order }

        for (purchase in statusWant) {
            purchaseListWithoutDoneAndSortedByStage.add(purchase)
        }

        for ((index, element) in purchaseListWithoutDoneAndSortedByStage.withIndex()) {
            if (index == 0) {
                val currentPeriod: Double = element.price / user.incomeRemainder

                val productPeriod = rounder(currentPeriod)

                val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                    10,
                    RoundingMode.HALF_EVEN
                ).toDouble()

                element.remind = productRemind
                element.realPeriod = productPeriod

            } else {

                val previousProduct = purchaseListWithoutDoneAndSortedByStage[index - 1]

                val currentPeriod: Double =
                    element.price / user.incomeRemainder - previousProduct.remind

                val productPeriod = rounder(currentPeriod)

                val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                    10,
                    RoundingMode.HALF_EVEN
                ).toDouble()

                element.remind = productRemind
                element.realPeriod = productPeriod + previousProduct.realPeriod

            }

            newPurchaseList.add(element)
        }

        return newPurchaseList

    }

    return purchaseList

}

fun rounder(x: Double): Int {
    if (x == round(x)) {
        return x.toInt()
    }

    return ceil(x).toInt()
}