package com.supter.data.network

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.MovieDetailResponse
import com.supter.data.response.MovieListResponse

interface PurchaseNetworkDataSource {
    val downloadedMovieList: LiveData<MovieListResponse>
    suspend fun fetchMovieList()

    val searchedMovieList: LiveData<out List<PurchaseEntity>>
    suspend fun searchMovie(query: String)

    val movie:LiveData<out MovieDetailResponse>
    suspend fun getMovie(movieId: Double)
}