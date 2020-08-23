package com.example.supter.data.network

import androidx.lifecycle.LiveData
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.data.response.MovieDetailResponse
import com.example.supter.data.response.MovieListResponse

interface MovieNetworkDataSource {
    val downloadedMovieList: LiveData<MovieListResponse>
    suspend fun fetchMovieList()

    val searchedMovieList: LiveData<out List<MovieEntity>>
    suspend fun searchMovie(query: String)

    val movie:LiveData<out MovieDetailResponse>
    suspend fun getMovie(movieId: Double)
}