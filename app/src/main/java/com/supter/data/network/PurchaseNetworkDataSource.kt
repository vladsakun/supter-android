package com.supter.data.network

import com.supter.data.body.AccountBody
import com.supter.data.body.PurchaseBody
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.data.response.purchase.GetPurchasesResponse
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse

interface PurchaseNetworkDataSource {
    suspend fun fetchPurchaseList(token: String): ResultWrapper<GetPurchasesResponse>

    suspend fun fetchPurchaseById(token: String, purchaseId: Int): ResultWrapper<DetailPurchaseResponse>

    suspend fun createPurchase(
            token: String,
            createPurchaseBody: PurchaseBody,
    ): ResultWrapper<CreatePurchaseResponse>

    suspend fun updatePurchase(
            token: String,
            purchaseId: Int,
            purchaseBody: PurchaseBody,
    ): ResultWrapper<UpdatePurchaseResponse>

    suspend fun deletePurchase(
            token: String,
            purchaseId: Int,
    ): ResultWrapper<MessageResponse>

    suspend fun postPurchaseIdsList(
            token:String,
            purchaseIdsList: List<Int>,
    ): ResultWrapper<MessageResponse>

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
    ):ResultWrapper<AccountResponse>

    suspend fun postAnswer(
        token: String,
        purchaseId: Int,
        questionId:Int,
        answer:String
    ): ResultWrapper<MessageResponse>
}