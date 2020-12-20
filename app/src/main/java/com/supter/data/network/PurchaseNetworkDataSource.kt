package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.body.AccountBody
import com.supter.data.body.PurchaseBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.*

interface PurchaseNetworkDataSource {
    suspend fun fetchPurchaseList(token: String): ResultWrapper<GetPurchasesResponse>

    suspend fun createPurchase(
            token: String,
            createPurchaseBody: PurchaseBody,
    ): ResultWrapper<CreatePurchaseResponse>

    suspend fun updatePurchase(
            token: String,
            purchaseId: Int,
            purchaseBody: PurchaseBody,
    ): ResultWrapper<UpdatePurchaseResponse>

    suspend fun deletePurchase(
            token: String,
            purchaseId: Int,
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseIdsList(
            token:String,
            purchaseIdsList: List<Int>,
    ): ResultWrapper<MessageResponse>

    suspend fun registerWithCoroutines(
            name: String,
            email: String,
            password: String,
    ): ResultWrapper<RegistrationResponse>

    suspend fun login(
            username: String,
            password: String,
    ): ResultWrapper<LoginResponse>

    suspend fun putUser(
            token: String,
            accountParams: AccountBody,
    ): ResultWrapper<AccountResponse>

    suspend fun fetchUser(
            token: String
    ):ResultWrapper<AccountResponse>
}