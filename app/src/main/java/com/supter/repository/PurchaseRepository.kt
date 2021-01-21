package com.supter.repository

import com.supter.data.body.ChangeStageBody
import com.supter.data.body.PurchaseBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.purchase.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface PurchaseRepository {

    fun getPurchaseList(userEntity: UserEntity): Flow<List<PurchaseEntity>>

    suspend fun getPurchaseFromApiById(purchaseEntity: PurchaseEntity): ResultWrapper<DetailPurchaseResponse>

    suspend fun getPurchaseById(purchaseId: Int): PurchaseEntity

    suspend fun createPurchase(createPurchaseBody: PurchaseBody): ResultWrapper<CreatePurchaseResponse>

    suspend fun upsertPurchase(purchaseEntity: PurchaseEntity)

    suspend fun deletePurchase(purchaseEntity: PurchaseEntity)

    suspend fun putPurchasesOrder(purchaseIdsList: List<Int>): ResultWrapper<MessageResponse>

    suspend fun changePurchaseStage(purchaseId: Int, changeStageBody: ChangeStageBody): ResultWrapper<CreatePurchaseResponse>

    suspend fun updateRemotePurchase(purchaseEntity: PurchaseEntity): ResultWrapper<UpdatePurchaseResponse>

    suspend fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>)

    suspend fun getUserFlow(): Flow<UserEntity?>

    suspend fun getUser(): UserEntity?

    suspend fun fetchUser(): ResultWrapper<AccountResponse>

    suspend fun sendStringAnswer(purchaseId:Int, questionId:Int, answer:String): ResultWrapper<AnswerQuestionResponse>

    suspend fun sendBooleanAnswer(purchaseId:Int, questionId:Int, answer:Boolean): ResultWrapper<AnswerQuestionResponse>

    suspend fun postPurchaseImage(purchaseId: Int, body: MultipartBody.Part): ResultWrapper<PurchaseData>
}