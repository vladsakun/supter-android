package com.supter.data.repository

import androidx.lifecycle.LiveData
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.MovieDetailResponse

interface PurchaseRepository {

    suspend fun getMovieList():LiveData<out List<PurchaseEntity>>
    suspend fun getSearchedMovies():LiveData<out List<PurchaseEntity>>
    suspend fun getMovie():LiveData<out MovieDetailResponse>

    suspend fun searchMovies(query: String)
    suspend fun fetchMovie(movieId: Double)
}