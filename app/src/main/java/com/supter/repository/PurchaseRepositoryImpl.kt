package com.supter.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.supter.R
import com.supter.data.body.ChangeStageBody
import com.supter.data.body.PurchaseBody
import com.supter.data.body.UpdatePurchaseBody
import com.supter.data.db.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.MessageResponse
import com.supter.data.response.ResultWrapper
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.purchase.*
import com.supter.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class PurchaseRepositoryImpl @Inject constructor(
    @ApplicationContext private var context: Context,
    private val dao: PurchaseDao,
    private val networkDataSource: PurchaseNetworkDataSource,
) : PurchaseRepository {

    private val TAG = "PurchaseRepositoryImpl"

    init {
        updateLocalUser()
    }

    private fun updateLocalUser() {
        GlobalScope.launch(Dispatchers.IO) {
            val accountResponse = fetchUser()

            if (accountResponse is ResultWrapper.Success) {
                with(accountResponse.value.data) {
                    if (incomeRemainder != null && period != null) {
                        dao.upsertUser(
                            convertAccountResponseToUserEntity(accountResponse.value)
                        )
                    }
                }
            }

        }
    }

    //Select all movies from db and return them
    override fun getPurchaseList(userEntity: UserEntity): Flow<List<PurchaseEntity>> {

        fetchPurchaseList()

        return dao.getPurchaseFlowList().map { updatePurchasesData(it, userEntity) }
    }

    override suspend fun getPurchaseFromApiById(purchaseEntity: PurchaseEntity): ResultWrapper<DetailPurchaseResponse> {
        return networkDataSource.fetchPurchaseById(SystemUtils.getToken(context), purchaseEntity.id)
    }

    override suspend fun getPurchaseById(purchaseId: Int): PurchaseEntity {
        return withContext(Dispatchers.IO) {
            dao.getPurchaseEntityById(purchaseId)
        }
    }

    //Fetch movies from api
    private fun fetchPurchaseList() {
        GlobalScope.launch(Dispatchers.IO) {
            val fetchedPurchaseList = networkDataSource.fetchPurchaseList(
                SystemUtils.getToken(context)
            )

            if (fetchedPurchaseList is ResultWrapper.Success) {
                upsertPurchaseList(
                    convertDataItemListToPurchaseEntityList(
                        fetchedPurchaseList.value.data
                    )
                )
            }
        }
    }

    override fun deleteAllPurchases() {
        GlobalScope.launch(Dispatchers.IO) {
            dao.deleteAllPurchses()
        }
    }

    override suspend fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        dao.upsertOneItem(purchaseEntity)
    }

    override suspend fun deletePurchase(purchaseEntity: PurchaseEntity) {
        val deleteResponse = networkDataSource.deletePurchase(
            SystemUtils.getToken(context),
            purchaseId = purchaseEntity.id
        )

        if (deleteResponse is ResultWrapper.Success) {
            dao.deletePurchaseEntity(purchaseEntity)
        }
    }

    override suspend fun putPurchasesOrder(
        purchaseIdsList: List<Int>,
        stage: String
    ): ResultWrapper<MessageResponse> {
        return networkDataSource.postPurchaseIdsList(
            SystemUtils.getToken(context),
            purchaseIdsList,
            stage
        )
    }

    override suspend fun changePurchaseStage(
        purchaseId: Int,
        changeStageBody: ChangeStageBody
    ): ResultWrapper<CreatePurchaseResponse> {
        return networkDataSource.postPurchaseStage(
            SystemUtils.getToken(context),
            purchaseId,
            changeStageBody
        )
    }

    override suspend fun updateRemotePurchase(purchaseEntity: PurchaseEntity): ResultWrapper<UpdatePurchaseResponse> {

        val updateResponse = networkDataSource.updatePurchase(
            SystemUtils.getToken(context),
            purchaseEntity.id,
            UpdatePurchaseBody(
                purchaseEntity.title,
                purchaseEntity.price,
                purchaseEntity.description,
                purchaseEntity.link
            )
        )

        if (updateResponse is ResultWrapper.Success) {
            upsertPurchase(purchaseEntity)
        }

        return updateResponse
    }

    override suspend fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>) {
        dao.upsert(purchaseEntityList)
    }

    override suspend fun getUserFlow(): Flow<UserEntity?> {
        return dao.getUserFlow()
    }

    override suspend fun getUser(): UserEntity? {
        return dao.getUser()
    }

    override suspend fun fetchUser(): ResultWrapper<AccountResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.fetchUser(SystemUtils.getToken(context))
        }
    }

    override suspend fun sendStringAnswer(
        purchaseId: Int,
        questionId: Int,
        answer: String
    ): ResultWrapper<AnswerQuestionResponse> {
        return networkDataSource.postStringAnswer(
            SystemUtils.getToken(context),
            purchaseId,
            questionId,
            answer
        )
    }

    override suspend fun sendBooleanAnswer(
        purchaseId: Int,
        questionId: Int,
        answer: Boolean
    ): ResultWrapper<AnswerQuestionResponse> {
        return networkDataSource.postBooleanAnswer(
            SystemUtils.getToken(context),
            purchaseId,
            questionId,
            answer
        )
    }

    override suspend fun postPurchaseImage(
        purchaseId: Int,
        body: MultipartBody.Part
    ): ResultWrapper<PurchaseData> {

        val resp =
            networkDataSource.postPurchaseImage(SystemUtils.getToken(context), purchaseId, body)

        if (resp is ResultWrapper.Success) {
            GlobalScope.launch(Dispatchers.IO) {
                with(resp.value) {
                    val byteArrayImage =
                        getByteArrayImage(context.getString(R.string.base_url) + image)
                    val purchaseEntity = dao.getPurchaseEntityById(purchaseId)
                    purchaseEntity.image = byteArrayImage
                    upsertPurchase(purchaseEntity)
                }
            }
        }

        return resp
    }

    override suspend fun createPurchase(createPurchaseBody: PurchaseBody): ResultWrapper<CreatePurchaseResponse> {
        val createPurchaseResponse =
            networkDataSource.createPurchase(SystemUtils.getToken(context), createPurchaseBody)

        if (createPurchaseResponse is ResultWrapper.Success) {
            createPurchaseResponse.value.data.apply {
                upsertPurchase(
                    PurchaseEntity(
                        id, title, price,
                        order, stage, potential,
                        description, remind = 0.0,
                        realPeriod = 0, thinkingTime = thinkingTime,
                        createdAt = createdAt, link = link, image = null,
                    )
                )
            }
        }

        return createPurchaseResponse
    }

}