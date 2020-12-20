package com.supter.data.network

import com.supter.data.body.AccountBody
import com.supter.data.body.LoginParams
import com.supter.data.body.PurchaseBody
import com.supter.data.body.RegistrationParams
import com.supter.data.response.*
import com.supter.utils.Authorization
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
        @Header(Authorization) token: String,
        @Body account: AccountBody
    ): AccountResponse

    //Read user
    @GET("account")
    suspend fun fetchUser(
            @Header(Authorization) token: String
    ):AccountResponse

    // Create purchase
    @POST("/purchases")
    suspend fun createPurchase(
        @Header(Authorization) token: String,
        @Body purchaseBody: PurchaseBody
    ):CreatePurchaseResponse

    // Get purchases
    @GET("/purchases")
    suspend fun getPurchasesList(
        @Header(Authorization) token: String
    ):GetPurchasesResponse

    // Update purchase
    @PUT("/purchases/{id}")
    suspend fun updatePurchase(
        @Header(Authorization) token:String,
        @Path("id") purchaseId:Int,
        @Body purchaseBody: PurchaseBody
    ): UpdatePurchaseResponse

    // Delete purchase
    @DELETE("/purchases/{id}")
    suspend fun deletePurchase(
        @Header(Authorization) token: String,
        @Path("id") purchaseId: Int
    ): MessageResponse


    @Headers("Content-Type: application/json")
    @PUT("/purchases/order")
    suspend fun putPurchasesOrder(
            @Header(Authorization) token:String,
            @Body ids: HashMap<String, List<Int>>
    ): MessageResponse


}