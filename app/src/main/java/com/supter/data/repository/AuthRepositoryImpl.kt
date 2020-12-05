package com.supter.data.repository

import android.content.Context
import com.supter.data.db.dao.Dao
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.LoginResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils.Companion.saveToken

class AuthRepositoryImpl(
    private var context: Context,
    private val dao: Dao,
    private val networkDataSource: PurchaseNetworkDataSource
) : AuthRepository {

    init {
        context = context.applicationContext
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return networkDataSource.registerWithCoroutines(name, email, password)
    }

    override suspend fun login(username: String, password: String):
            ResultWrapper<LoginResponse> {
        val response = networkDataSource.login(username, password)

        if(response is ResultWrapper.Success){
            saveToken(context, response.value.accessToken)
        }

        return response
    }

}