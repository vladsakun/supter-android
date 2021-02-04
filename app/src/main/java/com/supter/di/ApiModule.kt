package com.supter.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.data.network.Api
import com.supter.data.network.ConnectivityInterceptorImpl
import com.supter.utils.Authorization
import com.supter.utils.SystemUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object ApiModule {

    const val BASE_URL = "https://supter-api.demyan.net/"

    @Provides
    @Singleton
    fun provideApiClient(@ApplicationContext appContext: Context): Api {

        val requestInterceptor = Interceptor { chain ->
            val url = chain.request()
                .newBuilder()
                .header(Authorization, SystemUtils.getToken(appContext))
                .build()

            return@Interceptor chain.proceed(url)
        }

        val connectivityInterceptor = ConnectivityInterceptorImpl(appContext)

        //Build OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(requestInterceptor)
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