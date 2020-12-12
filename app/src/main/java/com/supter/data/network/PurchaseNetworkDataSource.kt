package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.body.AccountParams
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.AccountResponse
import com.supter.data.response.LoginResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper

interface PurchaseNetworkDataSource {
    val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
    suspend fun fetchPurchaseList()

    suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp>

    suspend fun login(username: String, password: String): ResultWrapper<LoginResponse>

    suspend fun putUser(
        token: String,
        accountParams: AccountParams
    ): ResultWrapper<AccountResponse>
}