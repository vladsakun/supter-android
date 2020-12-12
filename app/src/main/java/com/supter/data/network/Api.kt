package com.supter.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.data.body.AccountParams
import com.supter.data.body.UserAuthParams
import com.supter.data.body.UserParams
import com.supter.data.response.AccountResponse
import com.supter.data.response.LoginResponse
import com.supter.data.response.Resp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface Api {

    // Login
    @POST("auth/login")
    suspend fun loginUser(@Body user: UserAuthParams): LoginResponse

    //Register
    @POST("auth/register")
    suspend fun registerUser(@Body user: UserParams): Resp

    //Put user
    @PUT("account")
    suspend fun putUser(
        @Header("Authorization") token: String,
        @Body account: AccountParams
    ): AccountResponse

    companion object {

        const val BASE_URL = "https://supter-api.demyan.net/"

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