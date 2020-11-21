package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.Resp
import com.supter.data.response.ResponseWrapper
import com.supter.data.response.ResultWrapper

interface PurchaseNetworkDataSource {
    val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
    suspend fun fetchPurchaseList()

    suspend fun registerWithCoroutines(name: String, email: String, password: String) : ResultWrapper<Resp>
}