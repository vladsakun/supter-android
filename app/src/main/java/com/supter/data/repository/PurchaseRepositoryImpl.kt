package com.supter.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.supter.data.db.dao.Dao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PurchaseRepositoryImpl(
    private var context: Context,
    private val purchaseDao: Dao,
    private val movieNetworkDataSource: PurchaseNetworkDataSource

) : PurchaseRepository {

    init {
        context = context.applicationContext

        movieNetworkDataSource.apply {

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
            initMovieData()
            return@withContext purchaseDao.getListOfMovies()
        }
    }

    private suspend fun initMovieData() {
        fetchMovies()
    }

    //Fetch movies from api
    private suspend fun fetchMovies() {
        movieNetworkDataSource.fetchPurchaseList()
    }

    private fun insertDefaultPurchase() {
        GlobalScope.launch(Dispatchers.IO) {

            val DEFAULT_PURCHASE = PurchaseEntity(1, Status.WISH.ordinal, 8500.0, "Toyota Camry", null)
            purchaseDao.insertOneItem(DEFAULT_PURCHASE)
        }
    }
}