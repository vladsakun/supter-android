package com.supter.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.data.body.AccountBody
import com.supter.data.body.LoginParams
import com.supter.data.body.PurchaseBody
import com.supter.data.body.RegistrationParams
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.*
import com.supter.utils.exceptions.NoConnectivityException
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

    override suspend fun createPurchase(
        token: String,
        createPurchaseBody: PurchaseBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {api.createPurchase(token, createPurchaseBody)}
    }

    override suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return safeApiCall(Dispatchers.IO) { api.registerUser(RegistrationParams(name, email, password)) }
    }

    override suspend fun login(username: String, password: String): ResultWrapper<LoginResponse> {
        return safeApiCall(Dispatchers.IO) { api.loginUser(LoginParams(username, password)) }
    }

    override suspend fun putUser(
        token:String,
        accountParams: AccountBody
    ): ResultWrapper<AccountResponse> {
        return safeApiCall(Dispatchers.IO) {api.putUser(token, accountParams)}
    }


}