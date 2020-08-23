package com.example.supter.data.repository

import androidx.lifecycle.LiveData
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.data.response.MovieDetailResponse

interface MovieRepository {

    suspend fun getMovieList():LiveData<out List<MovieEntity>>
    suspend fun getSearchedMovies():LiveData<out List<MovieEntity>>
    suspend fun getMovie():LiveData<out MovieDetailResponse>

    suspend fun searchMovies(query: String)
    suspend fun fetchMovie(movieId: Double)
}