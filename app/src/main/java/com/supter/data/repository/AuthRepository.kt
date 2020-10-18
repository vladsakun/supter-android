package com.supter.data.repository

import androidx.lifecycle.LiveData

interface AuthRepository {

    val isAuthSuccessful: LiveData<Boolean>

    fun registerUser(name: String, email: String, password: String)

    suspend fun login(
        email: String,
        password: String
    )
}