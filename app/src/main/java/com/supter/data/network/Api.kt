package com.supter.data.network

import com.supter.data.body.AccountBody
import com.supter.data.body.LoginParams
import com.supter.data.body.PurchaseBody
import com.supter.data.body.RegistrationParams
import com.supter.data.response.*
import retrofit2.http.*

interface Api {

    // Login
    @POST("auth/login")
    suspend fun loginUser(@Body user: LoginParams): LoginResponse

    //Register
    @POST("auth/register")
    suspend fun registerUser(@Body user: RegistrationParams): RegistrationResponse

    //Put user
    @PUT("account")
    suspend fun putUser(
        @Header("Authorization") token: String,
        @Body account: AccountBody
    ): AccountResponse

    // Get purchases
    @GET("/purchases")
    suspend fun getPurchasesList(
        @Header("Authorization") token: String
    ):GetPurchasesResponse

    // Create purchase
    @POST("/purchases")
    suspend fun createPurchase(
        @Header("Authorization") token: String,
        @Body purchaseBody: PurchaseBody
    ):CreatePurchaseResponse

}