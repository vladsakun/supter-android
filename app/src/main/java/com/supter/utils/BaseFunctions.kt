package com.supter.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.model.PotentialItem
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.purchase.PurchaseData
import com.supter.data.response.purchase.QuestionsItem
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.round

private val TAG = "Supter"

//Download image from url and convert it to byte array
fun getByteArrayImage(url: String?): ByteArray? {
    if (url != null) {
        try {
            val imageUrl = URL(url)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val bis = BufferedInputStream(`is`)
            val buffer = ByteArrayOutputStream()
            //We create an array of bytes
            val data = ByteArray(50)
            var current = 0

            while (bis.read(data, 0, data.size).also { current = it } != -1) {
                buffer.write(data, 0, current)
            }
            return buffer.toByteArray()
        } catch (e: Exception) {
        }

    }
    // If could not download image from url return default poster
    return null
}

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
        resultList.add(convertDataItemToPurchaseEntity(dataItem))
    }

    return resultList
}

fun convertDataItemToPurchaseEntity(dataItem: PurchaseData): PurchaseEntity {
    with(dataItem) {

        val purchaseEntity = PurchaseEntity(
            id,
            title,
            price,
            order,
            stage,
            potential,
            description,
            remind = 0.0,
            realPeriod = 0,
            thinkingTime = thinkingTime,
            createdAt = createdAt,
            link = link,
            image = null,
        )

        dataItem.image?.let {
            purchaseEntity.image =
                getByteArrayImage("https://supter-api.demyan.net/" + dataItem.image)
        }

        return purchaseEntity
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

fun convertQuestionItemToPotentialItem(
    isDone: Boolean,
    questionsItem: QuestionsItem,
    answer: String?
): PotentialItem {
    return PotentialItem(
        isDone,
        questionsItem.title,
        answer,
        questionsItem.id,
        questionsItem.purchaseQuestion?.type
    )
}

/**
 * @param days
 */

fun getPrettyDate(days: Number): String {
    val time = days.toDouble() * 24 // convert days to hours

    if (time == 0.0) {
        return "0 minutes"
    }

    var prettyDate = when {

        time < 1.0 -> {
            (BigDecimal(time / 60).setScale(1, RoundingMode.HALF_EVEN)).toString() + " minutes"
        }

        time >= 1.0 && time < 24.0 -> {
            (BigDecimal(time).setScale(1, RoundingMode.HALF_EVEN)).toString() + " hours"
        }

        time >= 24.0 && time < (31.0 * 24) -> {
            (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
        }

        time >= (31.0 * 24) && time < (365 * 24) -> {
            (BigDecimal(time / (31 * 24)).setScale(
                1,
                RoundingMode.HALF_EVEN
            )).toString() + " months"
        }

        time >= (365 * 24) -> {
            (BigDecimal(time / (365 * 24)).setScale(
                1,
                RoundingMode.HALF_EVEN
            )).toString() + " years"
        }

        else -> {
            (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
        }
    }

    prettyDate = prettyDate.replace(".0", "")

    return prettyDate
}

fun updatePurchasesData(
    purchaseList: List<PurchaseEntity>,
    userEntity: UserEntity?
): List<PurchaseEntity> {

    userEntity?.let { user ->

        if (user.incomeRemainder != null && user.period != null && user.balance != null) {

            var userBalance: Float = user.balance

            val newPurchaseList = mutableListOf<PurchaseEntity>()
            val purchaseListWithoutDoneAndSortedByStage = mutableListOf<PurchaseEntity>()

            purchaseListWithoutDoneAndSortedByStage.addAll(purchaseList.filter { it.stage == STATUS_DECIDED }
                .sortedBy { it.order }
            )

            purchaseListWithoutDoneAndSortedByStage.addAll(purchaseList.filter { it.stage == STATUS_WANT }
                .sortedBy { it.order }
            )

            for ((index, element) in purchaseListWithoutDoneAndSortedByStage.withIndex()) {

                var priceWithBalance: Double = element.price - userBalance

                if (priceWithBalance <= 0) {
                    priceWithBalance = 0.0
                }

                userBalance = (userBalance - element.price).toFloat()

                if (userBalance <= 0) {
                    userBalance = 0f
                }

                Log.d(TAG, "priceWithBalance $priceWithBalance balance: $userBalance")

                if (index == 0) {

                    val currentPeriod: Double =
                        priceWithBalance / user.incomeRemainder

                    val realPeriod = rounder(currentPeriod)

                    val productRemind = BigDecimal(realPeriod - currentPeriod).setScale(
                        10,
                        RoundingMode.HALF_EVEN
                    ).toDouble()

                    element.remind = productRemind
                    element.realPeriod = realPeriod

                } else {

                    val previousProduct = purchaseListWithoutDoneAndSortedByStage[index - 1]

                    val currentPeriod: Double =
                        priceWithBalance / user.incomeRemainder - previousProduct.remind

                    val productPeriod = rounder(currentPeriod)

                    val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                        10,
                        RoundingMode.HALF_EVEN
                    ).toDouble()

                    element.remind = productRemind
                    element.realPeriod = productPeriod + previousProduct.realPeriod

                }

                Log.d(TAG, "realPeriod: ${element.realPeriod}")

                newPurchaseList.add(element)
            }

            return newPurchaseList

        }
        return purchaseList
    }

    return purchaseList
}

fun rounder(x: Double): Int {
    if (x == round(x)) {
        return x.toInt()
    }

    return ceil(x).toInt()
}

/**
 * @period account period
 * @realPeriod real period of purchase
 * @salaryDay account salary day
 *
 * @return period between today and salaryDay (in days)
 */
fun daysRealPeriod(period: Float, realPeriod: Int, salaryDay: Int): Float {

    if(realPeriod == 0){
        return 0f
    }

    val cal: Calendar = Calendar.getInstance()
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

    val realPeriod = period * realPeriod - dayOfMonth + salaryDay // in days

    return realPeriod

}

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
    val bitmap = createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}