package com.supter.data.network

import com.supter.data.body.*
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.data.response.purchase.GetPurchasesResponse
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse
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

    override suspend fun fetchPurchaseById(
        token: String,
        purchaseId: Int
    ): ResultWrapper<DetailPurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {api.getPurchase(token, purchaseId)}
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
        updatePurchaseBody: UpdatePurchaseBody
    ): ResultWrapper<UpdatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {api.updatePurchase(token, purchaseId, updatePurchaseBody)}
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

    override suspend fun postPurchaseStage(
        token: String,
        purchaseId: Int,
        changeStageBody: ChangeStageBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {api.updatePurchaseStage(token, purchaseId, changeStageBody)}
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

    override suspend fun postStringAnswer(
        token: String,
        purchaseId: Int,
        questionId: Int,
        text: String
    ): ResultWrapper<MessageResponse> {
        return safeApiCall(Dispatchers.IO) {api.postStringAnswer(token, purchaseId, questionId, text)}
    }

    override suspend fun postBooleanAnswer(
        token: String,
        purchaseId: Int,
        questionId: Int,
        isTrue: Boolean
    ): ResultWrapper<MessageResponse> {
        return safeApiCall(Dispatchers.IO) {api.postBooleanAnswer(token, purchaseId, questionId, isTrue)}
    }


}