package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.body.AccountBody
import com.supter.data.body.PurchaseBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.*

interface PurchaseNetworkDataSource {
    val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
    suspend fun fetchPurchaseList()

    suspend fun createPurchase(token: String, createPurchaseBody: PurchaseBody)
            : ResultWrapper<CreatePurchaseResponse>

    suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<RegistrationResponse>

    suspend fun login(username: String, password: String): ResultWrapper<LoginResponse>

    suspend fun putUser(
        token: String,
        accountParams: AccountBody
    ): ResultWrapper<AccountResponse>
}