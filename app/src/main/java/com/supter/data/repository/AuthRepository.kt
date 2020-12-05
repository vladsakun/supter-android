package com.supter.data.repository

import com.supter.data.response.LoginResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper

interface AuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp>

    suspend fun login(
        username: String,
        password: String
    ): ResultWrapper<LoginResponse>
}