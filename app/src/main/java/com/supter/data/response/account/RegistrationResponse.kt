package com.supter.data.response.account

data class RegistrationResponse(
    val accessToken: String,
    val user: User
)

data class User(
    val password: String,
    val period: Any,
    val name: Any,
    val balance: Any,
    val id: Int,
    val incomeRemainder: Any,
    val email: String
)