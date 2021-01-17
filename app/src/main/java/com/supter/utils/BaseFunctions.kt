package com.supter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.supter.BuildConfig
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.purchase.PurchaseData
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.round

private val TAG = "Supter"

fun stringToDate(dateString: String): Date? {
    // example date "2010-10-15T09:27:37Z"

    val dotIndex = dateString.indexOf('.')
    val trulyDateStr = dateString.substring(0, dotIndex) + "Z"
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)

    format.timeZone = TimeZone.getTimeZone("UTC")

    val date: Date
    date = try {
        format.parse(trulyDateStr)
    } catch (e: ParseException) {
        e.printStackTrace()
        Date()
    }

    format.timeZone = TimeZone.getDefault()
    val formattedDate = format.format(date)

    return format.parse(formattedDate)
}

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

    Log.e(TAG, message, e)

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
            thinkingTime = thinkingTime,
            createdAt = createdAt,
            link = link,
            image = null,
        )
    }
}

fun convertAccountResponseToUserEntity(accountResponse: AccountResponse): UserEntity {
    with(accountResponse.data) {
        return UserEntity(
            id,
            name,
            email,
            incomeRemainder?.toFloat(),
            balance?.toFloat(),
            period,
            salaryDay
        )
    }
}

/**
 * @param days
 */

fun getPrettyDate(days: Number): String {
    val time = days.toDouble() * 24 // convert days to hours

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

fun updatePurchasesData(
    purchaseList: List<PurchaseEntity>,
    user: UserEntity
): List<PurchaseEntity> {

    if (user.incomeRemainder != null && user.period != null) {

        val newPurchaseList = mutableListOf<PurchaseEntity>()
        val purchaseListWithoutDoneAndSortedByStage = mutableListOf<PurchaseEntity>()

        val processList =
            purchaseList.filter { it.stage == STATUS_DECIDED }.sortedBy { it.order }

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