package com.supter.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.data.body.UserParams
import com.supter.data.db.entity.PurchaseEntity
import com.supter.utils.exceptions.NoConnectivityException
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.Dispatchers

class PurchaseNetworkDataSourceImpl(
    private val api: Api
) : BaseNetworkDataSourceImpl(), PurchaseNetworkDataSource {

    private val CONNECTIVITY_TAG = "Connectivity"
    private val TAG = "PurchaseNetworkDataSour"

    private val _fetchedPurchaseList = MutableLiveData<List<PurchaseEntity>>()
    override val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
        get() = _fetchedPurchaseList

    //Fetch popular movie list
    override suspend fun fetchPurchaseList() {
        try {
            val list = ArrayList<PurchaseEntity>()
//            list.add(PurchaseEntity(1, 2000.0, "Laptop", null))
//            list.add(PurchaseEntity(2, 1000.0, "Chair", null))
//            list.add(PurchaseEntity(3, 1500.0, "Table", null))
//            list.add(PurchaseEntity(1, 2000.0, "Smartphone", null))
//            list.add(PurchaseEntity(3, 2500.0, "Car", null))

            _fetchedPurchaseList.postValue(list)

        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection", e)
        }
    }

    override suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return safeApiCall(Dispatchers.IO) { api.registerUser(UserParams(name, email, password)) }
    }
}