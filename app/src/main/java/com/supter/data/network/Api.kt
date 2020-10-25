package com.supter.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.data.body.UserParams
import com.supter.data.response.Resp
import com.supter.data.response.ResponseWrapper
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    //Register
    @POST("auth/register")
    fun registerAsync(@Body user: UserParams): Deferred<Response<Resp>>

    //Register
    @POST("auth/register")
    fun register(@Body user: UserParams): Call<Resp>

    //Register
    @POST("auth/register")
    suspend fun registerUser(@Body user: UserParams): ResponseWrapper<Resp>

    companion object {

        const val BASE_URL = "http://192.168.1.115:3000/api/"

        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): Api {

            //Build OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .build()

            //Build Retrofit
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }
}