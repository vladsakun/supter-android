package com.supter.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.supter.data.body.UserParams
import com.supter.data.db.dao.Dao
import com.supter.data.network.Api
import com.supter.data.network.Event
import com.supter.data.network.NetworkService
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.ErrorResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthRepositoryImpl(
    private var context: Context,
    private val dao: Dao,
    private val networkDataSource: PurchaseNetworkDataSource
) : AuthRepository {

    private val TAG = "AuthRepositoryImpl"

    init {
        context = context.applicationContext

        networkDataSource.apply {

            //Set observer on fetched access token
            registrationResp.observeForever { newUser ->
                persistFetchedToken(newUser)
            }

        }
    }

    private fun persistFetchedToken(newUser: Event<Resp>) {
        GlobalScope.launch(Dispatchers.IO) {
            newUser.data?.data?.access_token?.let { SystemUtils.saveToken(context, it) }
        }
    }

    override val registrationEventLiveData: LiveData<Event<Resp>>
        get() = networkDataSource.registrationResp

    override fun registerUser(name: String, email: String, password: String) {
        networkDataSource.register(name, email, password)
    }

    override suspend fun getRegisteredUser(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return networkDataSource.registerWithCoroutines(name, email, password)
    }

    override suspend fun login(email: String, password: String) {

    }

}