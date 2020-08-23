package com.example.supter.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.supter.convertMovieListResponseToListOfEntities
import com.example.supter.data.db.dao.MovieDao
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.data.network.MovieNetworkDataSource
import com.example.supter.data.response.MovieDetailResponse
import com.example.supter.data.response.MovieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieRepositoryImpl(
    private var context: Context,
    private val movieDao: MovieDao,
    private val movieNetworkDataSource: MovieNetworkDataSource

) : MovieRepository {

    init {
        context = context.applicationContext

        movieNetworkDataSource.apply {

            //Set observer on fetched popular movies
            downloadedMovieList.observeForever { newMovieResponse ->
                persistFetchedMovies(newMovieResponse)
            }

        }
    }

    //Add new movies to local db
    private fun persistFetchedMovies(newMovieResponse: MovieListResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            movieDao.upsert(convertMovieListResponseToListOfEntities(newMovieResponse))
        }
    }

    //Select all movies from db and return them
    override suspend fun getMovieList(): LiveData<List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            initMovieData()
            return@withContext movieDao.getListOfMovies()
        }
    }

    private suspend fun initMovieData() {
        fetchMovies()
    }

    //Fetch movies from api
    private suspend fun fetchMovies() {
        movieNetworkDataSource.fetchMovieList()
    }

    override suspend fun searchMovies(query: String) {
        movieNetworkDataSource.searchMovie(query)
    }

    override suspend fun fetchMovie(movieId: Double){
        movieNetworkDataSource.getMovie(movieId)
    }

    override suspend fun getMovie(): LiveData<out MovieDetailResponse> {
        return withContext(Dispatchers.IO) {
            movieNetworkDataSource.movie
        }
    }

    override suspend fun getSearchedMovies(): LiveData<out List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            return@withContext movieNetworkDataSource.searchedMovieList
        }
    }
}