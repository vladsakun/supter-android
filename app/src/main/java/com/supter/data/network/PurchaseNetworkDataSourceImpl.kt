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
import javax.inject.Inject

class PurchaseNetworkDataSourceImpl @Inject constructor(
    var api: Api
) : BaseNetworkDataSourceImpl(), PurchaseNetworkDataSource {

    private val CONNECTIVITY_TAG = "Connectivity"
    private val TAG = "PurchaseNetworkDataSour"

    override suspend fun fetchPurchaseList(
        token: String
    ): ResultWrapper<GetPurchasesResponse> {
        return safeApiCall(Dispatchers.IO) { api.getPurchasesList(token) }
    }

    override suspend fun createPurchase(
        token: String,
        createPurchaseBody: PurchaseBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) { api.createPurchase(token, createPurchaseBody) }
    }

    override suspend fun updatePurchase(
        token: String,
        purchaseId: Int,
        purchaseBody: PurchaseBody
    ): ResultWrapper<UpdatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {api.updatePurchase(token, purchaseId, purchaseBody)}
    }

    override suspend fun deletePurchase(
        token: String,
        purchaseId: Int
    ): ResultWrapper<MessageResponse> {
        return safeApiCall(Dispatchers.IO) {api.deletePurchase(token, purchaseId)}
    }

    override suspend fun postPurchaseIdsList(token: String, purchaseIdsList: List<Int>): ResultWrapper<MessageResponse> {
        val ids = hashMapOf("ids" to purchaseIdsList)
        return safeApiCall(Dispatchers.IO) {api.putPurchasesOrder(token, ids)}
    }

    override suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<RegistrationResponse> {
        return safeApiCall(Dispatchers.IO) {
            api.registerUser(
                RegistrationParams(
                    name,
                    email,
                    password
                )
            )
        }
    }

    override suspend fun login(username: String, password: String): ResultWrapper<LoginResponse> {
        return safeApiCall(Dispatchers.IO) { api.loginUser(LoginParams(username, password)) }
    }

    override suspend fun putUser(
        token: String,
        accountParams: AccountBody
    ): ResultWrapper<AccountResponse> {
        return safeApiCall(Dispatchers.IO) { api.putUser(token, accountParams) }
    }

    override suspend fun fetchUser(token: String): ResultWrapper<AccountResponse> {
        return safeApiCall(Dispatchers.IO) {api.fetchUser(token)}
    }


}