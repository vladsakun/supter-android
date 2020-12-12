package com.supter.repository

import androidx.lifecycle.LiveData
import com.supter.data.body.PurchaseBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {

    suspend fun getPurchaseList(): Flow<List<PurchaseEntity>>

    suspend fun createPurchase(createPurchaseBody: PurchaseBody):
            ResultWrapper<CreatePurchaseResponse>

    suspend fun upsertPurchase(purchaseEntity: PurchaseEntity)

    fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>)

    fun getUser(): Flow<UserEntity?>

}