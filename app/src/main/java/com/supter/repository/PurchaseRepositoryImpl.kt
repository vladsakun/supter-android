package com.supter.repository

import android.content.Context
import android.util.Log
import com.supter.data.body.PurchaseBody
import com.supter.data.db.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.AccountResponse
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.MessageResponse
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils
import com.supter.utils.convertDataItemListToPurchaseEntityList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
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
                if (accountResponse.value.data != null)
                    with(accountResponse.value.data) {
                        upsertUser(UserEntity(id, name, email, incomeRemainder, savings, period))
                    }
            }
        }
    }

    override fun upsertUser(userEntity: UserEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.upsertUser(userEntity)
        }
    }

    //Select all movies from db and return them
    override suspend fun getPurchaseList(): Flow<List<PurchaseEntity>> {

        fetchPurchaseList()

        return withContext(Dispatchers.IO) {
            return@withContext dao.getPurchaseFlowList()
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

    override suspend fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        dao.upsertOneItem(purchaseEntity)
    }

    override suspend fun deletePurchase(purchaseEntity: PurchaseEntity) {
        val deleteResponse = networkDataSource.deletePurchase(
                SystemUtils.getToken(context),
                purchaseId = purchaseEntity.id)

        if(deleteResponse is ResultWrapper.Success){
            dao.deletePurchaseEntity(purchaseEntity)
        }
    }

    override suspend fun putPurchasesOrder(purchaseIdsList: List<Int>): ResultWrapper<MessageResponse> {
        return networkDataSource.postPurchaseIdsList(SystemUtils.getToken(context), purchaseIdsList)
    }

    override fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.upsert(purchaseEntityList)
        }
    }

    override fun getLocalUser(): Flow<UserEntity?> {
        return dao.getUserFlow()
    }

    override suspend fun fetchUser(): ResultWrapper<AccountResponse> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "fetchUser: ${SystemUtils.getToken(context)}")
            return@withContext networkDataSource.fetchUser(SystemUtils.getToken(context))
        }
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
                                realPeriod = 0, null
                        )
                )
            }
        }

        return createPurchaseResponse
    }

}