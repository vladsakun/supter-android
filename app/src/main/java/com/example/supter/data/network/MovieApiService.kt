package com.example.supter.data.network

import com.example.supter.data.response.MovieDetailResponse
import com.example.supter.data.response.MovieListResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.*

interface MovieApiService {

    //Get popular movies
    @GET("popular")
    fun getMoviesList(): Deferred<MovieListResponse>

    @GET("trending/movie/week")
    fun getTrendingMovies(): Deferred<MovieListResponse>

    @GET("search/movie")
    fun serchMovie(@Query("query") query: String): Deferred<MovieListResponse>

    @GET("movie/{movie_id}")
    fun getMovie(@Path("movie_id") movie_id: Double): Deferred<MovieDetailResponse>

    @GET
    fun loadImage(@Url url:String): Call<ResponseBody>

    companion object {

        private const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185"
        private const val API_KEY = "78668797853c3b31320011e0e411b0a6"
        private const val DEFAULT_PAGE_COUNT = "1"

        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): MovieApiService {
            val requestInterceptor = Interceptor { chain ->

                //Add default query params for requests
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .addQueryParameter("language", Locale.getDefault().toLanguageTag())
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            //Build OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            //Build Retrofit
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MovieApiService::class.java)
        }
    }
}