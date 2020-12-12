package com.supter.data.response

data class Resp(
    val `data`: RegisterResponse
)

data class RegisterResponse(
    val access_token: String,
    val user: User
)

data class User(
    val email: String,
    val id: Int,
    val name: String
)