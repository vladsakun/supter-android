package com.supter.data.repository

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity

interface PurchaseRepository {

    suspend fun getPurchaseList():LiveData<out List<PurchaseEntity>>
}