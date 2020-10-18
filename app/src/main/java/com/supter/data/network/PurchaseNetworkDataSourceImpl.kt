package com.supter.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.utils.convertMovieListResponseToListOfEntities
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.exceptions.NoConnectivityException
import com.supter.data.response.PurchaseDetailResponse
import com.supter.data.response.PurchaseListResponse

class PurchaseNetworkDataSourceImpl(
    private val purchaseApi: PurchaseApiService
) : PurchaseNetworkDataSource {

    val CONNECTIVITY_TAG = "Connectivity"

    private val _fetchedPurchaseList = MutableLiveData<List<PurchaseEntity>>()
    override val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
        get() = _fetchedPurchaseList

    //Fetch popular movie list
    override suspend fun fetchPurchaseList() {
        try {
            val list = ArrayList<PurchaseEntity>()
//
//            list.add(PurchaseEntity(1, 2000.0, "Laptop", null))
//            list.add(PurchaseEntity(2, 1000.0, "Chair", null))
//            list.add(PurchaseEntity(3, 1500.0, "Table", null))
//            list.add(PurchaseEntity(1, 2000.0, "Smartphone", null))
//            list.add(PurchaseEntity(3, 2500.0, "Car", null))

            _fetchedPurchaseList.postValue(list)

        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection", e)
        }
    }

    private val _searchedMovieList = MutableLiveData<List<PurchaseEntity>>()
    override val searchedMovieList: LiveData<out List<PurchaseEntity>>
        get() = _searchedMovieList

    //Search movie
    override suspend fun searchMovie(query: String) {
        try {
            val fetchedMovies = purchaseApi.serchMovie(query).await()
            _searchedMovieList.postValue(
                convertMovieListResponseToListOfEntities(
                    fetchedMovies
                )
            )
        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection: ", e)
        }
    }

    private val _movie = MutableLiveData<PurchaseDetailResponse>()
    override val movie: LiveData<out PurchaseDetailResponse>
        get() = _movie

    //Get movie
    override suspend fun getMovie(movieId: Double) {
        try {
            val fetchedMovie = purchaseApi.getMovie(movieId).await()
            _movie.postValue(fetchedMovie)
        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection: ", e)
        }
    }
}