package com.supter.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.supter.convertMovieListResponseToListOfEntities
import com.supter.data.db.dao.PurchaseDao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.MovieDetailResponse
import com.supter.data.response.MovieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PurchaseRepositoryImpl(
    private var context: Context,
    private val movieDao: PurchaseDao,
    private val movieNetworkDataSource: PurchaseNetworkDataSource

) : PurchaseRepository {

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
    override suspend fun getMovieList(): LiveData<List<PurchaseEntity>> {
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

    override suspend fun getSearchedMovies(): LiveData<out List<PurchaseEntity>> {
        return withContext(Dispatchers.IO) {
            return@withContext movieNetworkDataSource.searchedMovieList
        }
    }
}