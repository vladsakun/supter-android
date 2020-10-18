package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.PurchaseDetailResponse
import com.supter.data.response.PurchaseListResponse

interface PurchaseNetworkDataSource {
    val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
    suspend fun fetchPurchaseList()

    val searchedMovieList: LiveData<out List<PurchaseEntity>>
    suspend fun searchMovie(query: String)

    val movie:LiveData<out PurchaseDetailResponse>
    suspend fun getMovie(movieId: Double)
}