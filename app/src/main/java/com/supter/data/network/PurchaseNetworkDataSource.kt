package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.Resp

interface PurchaseNetworkDataSource {
    val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
    suspend fun fetchPurchaseList()

    val registrationResp: LiveData<Resp>
    suspend fun register(name: String, email: String, password: String)


}