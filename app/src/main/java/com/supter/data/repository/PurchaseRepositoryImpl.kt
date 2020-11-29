package com.supter.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.supter.data.db.dao.Dao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.network.PurchaseNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PurchaseRepositoryImpl(
    private var context: Context,
    private val purchaseDao: Dao,
    private val networkDataSource: PurchaseNetworkDataSource

) : PurchaseRepository {

    init {
        context = context.applicationContext

        networkDataSource.apply {

            //Set observer on fetched purchases
            fetchedPurchaseList.observeForever { newPurchaseResponse ->
                persistFetchedPurchases(newPurchaseResponse)
            }

        }

    }

    //Add new movies to local db
    private fun persistFetchedPurchases(newPurchaseList: List<PurchaseEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            purchaseDao.upsert(newPurchaseList)
        }
    }

    //Select all movies from db and return them
    override suspend fun getPurchaseList(): LiveData<List<PurchaseEntity>> {
        return withContext(Dispatchers.IO) {
            initPurchaseData()
            return@withContext purchaseDao.getPurchaseLiveDataList()
        }
    }

    override suspend fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        purchaseDao.upsertOneItem(purchaseEntity)
    }

    private suspend fun initPurchaseData() {
        fetchPurchaseList()
    }

    //Fetch movies from api
    private suspend fun fetchPurchaseList() {
        networkDataSource.fetchPurchaseList()
    }

}