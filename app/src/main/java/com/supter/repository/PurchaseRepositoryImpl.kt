package com.supter.repository

import android.content.Context
import android.util.Log
import com.supter.data.body.PurchaseBody
import com.supter.data.db.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.data.response.purchase.PurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.utils.SystemUtils
import com.supter.utils.convertDataItemListToPurchaseEntityList
import com.supter.utils.updatePurchasesData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                            UserEntity(
                                id,
                                name,
                                email,
                                incomeRemainder,
                                savings,
                                period
                            )
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

    override suspend fun getPurchaseFromApiById(purchaseEntity: PurchaseEntity): ResultWrapper<PurchaseResponse> {
        return networkDataSource.fetchPurchaseById(SystemUtils.getToken(context), purchaseEntity.id)
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

    override suspend fun putPurchasesOrder(purchaseIdsList: List<Int>): ResultWrapper<MessageResponse> {
        return networkDataSource.postPurchaseIdsList(SystemUtils.getToken(context), purchaseIdsList)
    }

    override suspend fun updateRemotePurchase(purchaseEntity: PurchaseEntity): ResultWrapper<UpdatePurchaseResponse> {

        val updateResponse = networkDataSource.updatePurchase(
            SystemUtils.getToken(context),
            purchaseEntity.id,
            PurchaseBody(purchaseEntity.title, purchaseEntity.price)
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
            Log.d(TAG, "fetchUser: ${SystemUtils.getToken(context)}")
            return@withContext networkDataSource.fetchUser(SystemUtils.getToken(context))
        }
    }

    override suspend fun sendAnswer(purchaseId: Int, questionId: Int, answer: String): ResultWrapper<MessageResponse> {
        return networkDataSource.postAnswer(SystemUtils.getToken(context), purchaseId, questionId, answer)
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
                        description, null, remind = 0.0,
                        realPeriod = 0, thinkingTime, createdAt, null
                    )
                )
            }
        }

        return createPurchaseResponse
    }

}