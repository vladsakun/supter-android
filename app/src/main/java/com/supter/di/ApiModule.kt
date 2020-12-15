package com.supter.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.data.network.Api
import com.supter.data.network.ConnectivityInterceptor
import com.supter.data.network.ConnectivityInterceptorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class ConnectivityInterceptorModule {
    @Singleton
    @Binds
    abstract fun bindConnectivityInterceptor(impl: ConnectivityInterceptorImpl): ConnectivityInterceptor
}

@InstallIn(ApplicationComponent::class)
@Module
object ApiModule {

    const val BASE_URL = "https://supter-api.demyan.net/"
//    @Inject lateinit var connectivityInterceptor: ConnectivityInterceptor

    @Provides
    @Singleton
    fun provideApiClient(): Api {

        //Build OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(connectivityInterceptor)
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