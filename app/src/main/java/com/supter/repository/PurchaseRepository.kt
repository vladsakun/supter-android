package com.supter.repository

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity

interface PurchaseRepository {

    suspend fun getPurchaseList():LiveData<out List<PurchaseEntity>>

    suspend fun upsertPurchase(purchaseEntity: PurchaseEntity)
}