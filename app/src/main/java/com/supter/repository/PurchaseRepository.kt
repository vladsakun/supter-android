package com.supter.repository

import androidx.lifecycle.LiveData
import com.supter.data.body.PurchaseBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.*
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {

    suspend fun getPurchaseList(): Flow<List<PurchaseEntity>>

    suspend fun createPurchase(createPurchaseBody: PurchaseBody): ResultWrapper<CreatePurchaseResponse>

    suspend fun upsertPurchase(purchaseEntity: PurchaseEntity)

    suspend fun deletePurchase(purchaseEntity: PurchaseEntity)

    suspend fun putPurchasesOrder(purchaseIdsList: List<Int>): ResultWrapper<MessageResponse>

    suspend fun updateRemotePurchase(purchaseEntity: PurchaseEntity):ResultWrapper<UpdatePurchaseResponse>

    suspend fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>)

    suspend fun getLocalUser(): Flow<UserEntity?>

    suspend fun fetchUser(): ResultWrapper<AccountResponse>

    suspend fun upsertUser(userEntity: UserEntity)

}