package com.supter.repository

import android.content.Context
import com.supter.data.body.PurchaseBody
import com.supter.data.db.dao.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils
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
    private val networkDataSource: PurchaseNetworkDataSource

) : PurchaseRepository {

    private val TAG = "PurchaseRepositoryImpl"

    init {
        context = context.applicationContext

        networkDataSource.apply {

            //Set observer on fetched purchases
            fetchedPurchaseList.observeForever { newPurchaseResponse ->
                upsertPurchaseList(newPurchaseResponse)
            }

        }

    }

    //Select all movies from db and return them
    override suspend fun getPurchaseList(): Flow<List<PurchaseEntity>> {
        return withContext(Dispatchers.IO) {
            initPurchaseData()
            return@withContext dao.getPurchaseFlowList()
        }
    }


    override suspend fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        dao.upsertOneItem(purchaseEntity)
    }

    override fun upsertPurchaseList(purchaseEntityList: List<PurchaseEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.upsert(purchaseEntityList)
        }
    }

    override fun getUser(): Flow<UserEntity?> {
        return dao.getUserFlow()
    }

    private suspend fun initPurchaseData() {
        fetchPurchaseList()
    }

    //Fetch movies from api
    private suspend fun fetchPurchaseList() {
        networkDataSource.fetchPurchaseList()
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