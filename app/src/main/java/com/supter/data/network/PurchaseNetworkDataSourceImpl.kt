package com.supter.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.convertMovieListResponseToListOfEntities
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.exceptions.NoConnectivityException
import com.supter.data.response.MovieDetailResponse
import com.supter.data.response.MovieListResponse

class PurchaseNetworkDataSourceImpl(
    private val purchaseApi: PurchaseApiService
) : PurchaseNetworkDataSource {

    val CONNECTIVITY_TAG = "Connectivity"

    private val _downloadedMovieList = MutableLiveData<MovieListResponse>()
    override val downloadedMovieList: LiveData<MovieListResponse>
        get() = _downloadedMovieList

    //Fetch popular movie list
    override suspend fun fetchMovieList() {
        try {
            val fetchedSunInfo = purchaseApi.getTrendingMovies().await()
            _downloadedMovieList.postValue(fetchedSunInfo)
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
            _searchedMovieList.postValue(convertMovieListResponseToListOfEntities(fetchedMovies))
        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection: ", e)
        }
    }

    private val _movie = MutableLiveData<MovieDetailResponse>()
    override val movie: LiveData<out MovieDetailResponse>
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