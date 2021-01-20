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

interface PurchaseNetworkDataSource {
    suspend fun fetchPurchaseList(token: String): ResultWrapper<GetPurchasesResponse>

    suspend fun fetchPurchaseById(
        token: String,
        purchaseId: Int
    ): ResultWrapper<DetailPurchaseResponse>

    suspend fun createPurchase(
        token: String,
        createPurchaseBody: PurchaseBody,
    ): ResultWrapper<CreatePurchaseResponse>

    suspend fun updatePurchase(
        token: String,
        purchaseId: Int,
        updatePurchaseBody: UpdatePurchaseBody,
    ): ResultWrapper<UpdatePurchaseResponse>

    suspend fun deletePurchase(
        token: String,
        purchaseId: Int,
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseIdsList(
        token: String,
        purchaseIdsList: List<Int>,
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseStage(
        token: String,
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
        token: String,
        accountParams: AccountBody,
    ): ResultWrapper<AccountResponse>

    suspend fun fetchUser(
        token: String
    ): ResultWrapper<AccountResponse>

    suspend fun postStringAnswer(
        token: String,
        purchaseId: Int,
        questionId: Int,
        text: String,
    ): ResultWrapper<AnswerQuestionResponse>

    suspend fun postBooleanAnswer(
        token: String,
        purchaseId: Int,
        questionId: Int,
        isTrue: Boolean,
    ): ResultWrapper<AnswerQuestionResponse>
}