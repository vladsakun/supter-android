package com.supter.data.repository

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.PurchaseDetailResponse

interface PurchaseRepository {

    suspend fun getPurchaseList():LiveData<out List<PurchaseEntity>>
}