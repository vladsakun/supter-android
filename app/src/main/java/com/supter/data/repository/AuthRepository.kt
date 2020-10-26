package com.supter.data.repository

import android.util.EventLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supter.data.network.Event
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper

interface AuthRepository {

    val registrationEventLiveData: LiveData<Event<Resp>>

    fun registerUser(name: String, email: String, password: String)
    suspend fun getRegisteredUser(name: String, email: String, password: String): ResultWrapper<Resp>

    suspend fun login(
        email: String,
        password: String
    )
}