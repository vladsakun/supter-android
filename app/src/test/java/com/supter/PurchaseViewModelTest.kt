package com.supter

import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.utils.STAGE_WANT
import com.supter.utils.rounder
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class PurchaseViewModelTest {
    data class TestPurchaseEntity(
        val price: Double,
        var remind: Double? = null,
        var realPeriod: Int? = null
    )

    @Test
    fun `generate real period for a list of purchase entities`() {
        val testUser = UserEntity(0, "test", "test", 10000f, 0f, 30.4f, 1)

        val testPurchaseEntityList = listOf(
            TestPurchaseEntity(30000.0),
            TestPurchaseEntity(12000.0),
            TestPurchaseEntity(100.0),
            TestPurchaseEntity(3000.0),
            TestPurchaseEntity(13000.0)
        )

        var userBalance: Float = testUser.balance!!

        val newPurchaseList = mutableListOf<TestPurchaseEntity>()

        for ((index, element) in testPurchaseEntityList.withIndex()) {

            var priceWithBalance: Double = element.price - userBalance

            if (priceWithBalance <= 0) {
                priceWithBalance = 0.0
            }

            userBalance = (userBalance - element.price).toFloat()

            if (userBalance <= 0) {
                userBalance = 0f
            }

            if (index == 0) {

                val currentPeriod: Double =
                    priceWithBalance / testUser.incomeRemainder!!

                val realPeriod = rounder(currentPeriod)

                val productRemind = BigDecimal(realPeriod - currentPeriod).setScale(
                    10,
                    RoundingMode.HALF_EVEN
                ).toDouble()

                element.remind = productRemind
                element.realPeriod = realPeriod

            } else {

                val previousProduct = testPurchaseEntityList[index - 1]

                val currentPeriod: Double =
                    priceWithBalance / testUser.incomeRemainder!! - previousProduct.remind!!

                val productPeriod = rounder(currentPeriod)

                val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                    10,
                    RoundingMode.HALF_EVEN
                ).toDouble()

                element.remind = productRemind
                element.realPeriod = productPeriod + previousProduct.realPeriod!!

            }

            newPurchaseList.add(element)
        }

        val testList = mutableListOf<PurchaseEntity>()

        for (testPurchase in testPurchaseEntityList) {
            with(testPurchase) {
                testList.add(
                    PurchaseEntity(
                        0,
                        "test",
                        price,
                        0,
                        STAGE_WANT,
                        0f,
                        "desc",
                        remind!!,
                        realPeriod!!,
                        "think",
                        "created",
                        null,
                        null
                    )
                )
            }
        }

        assert(newPurchaseList[0].realPeriod == 3)
        assert(newPurchaseList[0].remind == 0.0)
        assert(newPurchaseList[1].realPeriod == 5)
        assert(newPurchaseList[1].remind == 0.8)
        assert(newPurchaseList[4].remind == 0.19)

    }

}