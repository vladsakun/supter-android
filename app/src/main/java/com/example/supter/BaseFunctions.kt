package com.example.supter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.data.network.MovieApiService
import com.example.supter.data.response.MovieListResponse
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


private val TAG = "BaseFunctions"

fun isOnline(context: Context): Boolean {
    var isOnline = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    isOnline = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return isOnline
}

//Download image from url and convert it to byte array
fun getByteArrayImage(url: String?): ByteArray? {
    if (url != null) {
        try {
            val imageUrl = URL(url)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val bis = BufferedInputStream(`is`)
            val buffer = ByteArrayOutputStream()
            //We create an array of bytes
            val data = ByteArray(50)
            var current = 0

            while (bis.read(data, 0, data.size).also { current = it } != -1) {
                buffer.write(data, 0, current)
            }
            return buffer.toByteArray()
        } catch (e: Exception) {
            Log.d("ImageManager", "Error: $e")
        }

    }
    // If could not download image from url return default poster
    return null
}

//Convert movies response to list of movie entities for db
fun convertMovieListResponseToListOfEntities(newMovieResponse: MovieListResponse): List<MovieEntity> {
    val movieEntityList: ArrayList<MovieEntity> = arrayListOf()

    for (movie in newMovieResponse.results) {

        movieEntityList.add(
            MovieEntity(
                movie.id,
                movie.popularity,
                movie.adult,
                movie.original_title,
                movie.title,
                movie.overview,
                movie.release_date,
                movie.poster_path,
                movie.vote_average,
                getByteArrayImage(MovieApiService.IMAGE_BASE_URL + movie.poster_path)
            )
        )

    }
    return movieEntityList
}