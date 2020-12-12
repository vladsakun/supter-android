package com.supter.repository

import com.supter.data.db.entity.UserEntity
import com.supter.data.response.AccountResponse
import com.supter.data.response.LoginResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp>

    suspend fun login(
        username: String,
        password: String
    ): ResultWrapper<LoginResponse>

    fun getUser(): Flow<UserEntity?>

    fun upsertUser(
        userEntity: UserEntity
    )

    // Update user on the server side
    suspend fun putUser(
        name: String,
        incomeRemainder:Double,
        savings:Double,
        period:Double
    ): ResultWrapper<AccountResponse>
}