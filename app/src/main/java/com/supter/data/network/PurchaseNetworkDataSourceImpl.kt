package com.supter.data.network

import com.supter.data.body.*
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.*
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import javax.inject.Inject

class PurchaseNetworkDataSourceImpl @Inject constructor(
    var api: Api
) : BaseNetworkDataSourceImpl(), PurchaseNetworkDataSource {

    private val CONNECTIVITY_TAG = "Connectivity"
    private val TAG = "PurchaseNetworkDataSour"

    override suspend fun fetchPurchaseList(
    ): ResultWrapper<GetPurchasesResponse> {
        return safeApiCall(Dispatchers.IO) { api.getPurchasesList() }
    }

    override suspend fun fetchPurchaseById(

        purchaseId: Int
    ): ResultWrapper<DetailPurchaseResponse> {
        return safeApiCall(Dispatchers.IO) { api.getPurchase(purchaseId) }
    }

    override suspend fun createPurchase(

        createPurchaseBody: PurchaseBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) { api.createPurchase(createPurchaseBody) }
    }

    override suspend fun updatePurchase(

        purchaseId: Int,
        updatePurchaseBody: UpdatePurchaseBody
    ): ResultWrapper<UpdatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {
            api.updatePurchase(
                purchaseId,
                updatePurchaseBody
            )
        }
    }

    override suspend fun deletePurchase(

        purchaseId: Int
    ): ResultWrapper<MessageResponse> {
        return safeApiCall(Dispatchers.IO) { api.deletePurchase(purchaseId) }
    }

    override suspend fun postPurchaseIdsList(

        purchaseIdsList: List<Int>,
        stage: String
    ): ResultWrapper<MessageResponse> {
        val ids: HashMap<String, Any> = hashMapOf("ids" to purchaseIdsList)
        ids["stage"] = stage
        return safeApiCall(Dispatchers.IO) { api.putPurchasesOrder(ids) }
    }

    override suspend fun postPurchaseStage(

        purchaseId: Int,
        changeStageBody: ChangeStageBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return safeApiCall(Dispatchers.IO) {
            api.updatePurchaseStage(

                purchaseId,
                changeStageBody
            )
        }
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
        accountParams: AccountBody
    ): ResultWrapper<AccountResponse> {
        return safeApiCall(Dispatchers.IO) { api.putUser(accountParams) }
    }

    override suspend fun fetchUser(): ResultWrapper<AccountResponse> {
        return safeApiCall(Dispatchers.IO) { api.fetchUser() }
    }

    override suspend fun postStringAnswer(

        purchaseId: Int,
        questionId: Int,
        text: String
    ): ResultWrapper<AnswerQuestionResponse> {
        return safeApiCall(Dispatchers.IO) {
            api.postStringAnswer(
                purchaseId,
                questionId,
                text
            )
        }
    }

    override suspend fun postBooleanAnswer(

        purchaseId: Int,
        questionId: Int,
        isTrue: Boolean
    ): ResultWrapper<AnswerQuestionResponse> {
        return safeApiCall(Dispatchers.IO) {
            api.postBooleanAnswer(
                purchaseId,
                questionId,
                isTrue
            )
        }
    }

    override suspend fun postPurchaseImage(

        purchaseId: Int,
        body: MultipartBody.Part
    ): ResultWrapper<PurchaseData> {
        return safeApiCall(Dispatchers.IO) { api.postPurchaseImage(purchaseId, body) }
    }
}