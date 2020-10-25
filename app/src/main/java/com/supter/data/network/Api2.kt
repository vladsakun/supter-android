package com.supter.data.network

import com.supter.data.body.UserParams
import com.supter.data.response.Resp
import com.supter.data.response.ResponseWrapper
import retrofit2.http.Body
import retrofit2.http.POST

interface Api2 {
    //Register
    @POST("auth/register")
    suspend fun registerUser(@Body user: UserParams): ResponseWrapper<Resp>
}