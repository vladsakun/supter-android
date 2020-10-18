package com.supter.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.data.db.dao.Dao
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.Resp
import com.supter.utils.SystemUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                if (newUser != null) {
                    Log.d(TAG, "New User: $newUser")
                    persistFetchedToken(newUser)
                }
            }

        }
    }

    private fun persistFetchedToken(newUser: Resp) {
        GlobalScope.launch(Dispatchers.IO) {
            SystemUtils.saveToken(context, newUser.data.access_token)
            _isAuthSuccessful.postValue(true)
        }
    }

    val _isAuthSuccessful = MutableLiveData<Boolean>()
    override val isAuthSuccessful: LiveData<Boolean>
        get() = _isAuthSuccessful

    override fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            networkDataSource.register(name, email, password)
        }

    }

    override suspend fun login(email: String, password: String) {
        TODO("Not yet implemented")
    }

}