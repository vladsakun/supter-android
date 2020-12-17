package com.supter.repository

import android.content.Context
import com.supter.data.body.PurchaseBody
import com.supter.data.db.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils
import com.supter.utils.converDataItemListToPurchaseEntityList
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

    //Select all movies from db and return them
    override suspend fun getPurchaseList(): Flow<List<PurchaseEntity>> {
        return withContext(Dispatchers.IO) {
            fetchPurchaseList()
            return@withContext dao.getPurchaseFlowList()
        }
    }

    //Fetch movies from api
    private suspend fun fetchPurchaseList() {
        val fetchedPurchaseList = networkDataSource.fetchPurchaseList(
            SystemUtils.getToken(context)
        )

        if (fetchedPurchaseList is ResultWrapper.Success) {
            upsertPurchaseList(
                converDataItemListToPurchaseEntityList(
                    fetchedPurchaseList.value.data
                )
            )
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