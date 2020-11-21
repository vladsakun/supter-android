package com.supter.data.repository

import android.content.Context
import com.supter.data.db.dao.Dao
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper

class AuthRepositoryImpl(
    private var context: Context,
    private val dao: Dao,
    private val networkDataSource: PurchaseNetworkDataSource
) : AuthRepository {

    private val TAG = "AuthRepositoryImpl"

    init {
        context = context.applicationContext

        networkDataSource.apply {

        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return networkDataSource.registerWithCoroutines(name, email, password)
    }

    override suspend fun login(email: String, password: String) {

    }

}