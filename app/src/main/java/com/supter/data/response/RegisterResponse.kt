package com.supter.data.response

data class RegisterResponse(
    val access_token: String,
    val user: User
)