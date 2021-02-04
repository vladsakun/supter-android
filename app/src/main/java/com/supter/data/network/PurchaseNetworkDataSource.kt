package com.supter.data.network

import com.supter.data.body.AccountBody
import com.supter.data.body.ChangeStageBody
import com.supter.data.body.PurchaseBody
import com.supter.data.body.UpdatePurchaseBody
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.*
import okhttp3.MultipartBody

interface PurchaseNetworkDataSource {
    suspend fun fetchPurchaseList(): ResultWrapper<GetPurchasesResponse>

    suspend fun fetchPurchaseById(
        
        purchaseId: Int
    ): ResultWrapper<DetailPurchaseResponse>

    suspend fun createPurchase(
        
        createPurchaseBody: PurchaseBody,
    ): ResultWrapper<CreatePurchaseResponse>

    suspend fun updatePurchase(
        
        purchaseId: Int,
        updatePurchaseBody: UpdatePurchaseBody,
    ): ResultWrapper<UpdatePurchaseResponse>

    suspend fun deletePurchase(
        
        purchaseId: Int,
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseIdsList(
        
        purchaseIdsList: List<Int>,
        stage: String
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseStage(
        
        purchaseId: Int,
        changeStageBody: ChangeStageBody
    ): ResultWrapper<CreatePurchaseResponse>

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
        accountParams: AccountBody,
    ): ResultWrapper<AccountResponse>

    suspend fun fetchUser(
    ): ResultWrapper<AccountResponse>

    suspend fun postStringAnswer(
        purchaseId: Int,
        questionId: Int,
        text: String,
    ): ResultWrapper<AnswerQuestionResponse>

    suspend fun postBooleanAnswer(
        
        purchaseId: Int,
        questionId: Int,
        isTrue: Boolean,
    ): ResultWrapper<AnswerQuestionResponse>

    suspend fun postPurchaseImage(
        
        purchaseId: Int,
        body: MultipartBody.Part
    ): ResultWrapper<PurchaseData>
}