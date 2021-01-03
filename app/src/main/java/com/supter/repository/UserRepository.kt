package com.supter.repository

import com.supter.data.db.entity.UserEntity
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<RegistrationResponse>

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
        period:Double,
        dateOfSalaryComing:Int
    ): ResultWrapper<AccountResponse>

    fun clearDB()
}