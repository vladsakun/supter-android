package com.supter.data.network

import com.supter.data.body.*
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.data.response.purchase.GetPurchasesResponse
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.utils.Authorization
import retrofit2.http.*

interface Api {

    // ******************************************Account*********************************************

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
    ): AccountResponse

    // *************************************************Purchase*********************************************

    // Create purchase
    @POST("/purchases")
    suspend fun createPurchase(
        @Header(Authorization) token: String,
        @Body purchaseBody: PurchaseBody
    ): CreatePurchaseResponse

    // Get purchases
    @GET("/purchases")
    suspend fun getPurchasesList(
        @Header(Authorization) token: String
    ): GetPurchasesResponse

    // Get purchase by id
    @GET("/purchases/{id}")
    suspend fun getPurchase(
        @Header(Authorization) token: String,
        @Path("id") purchaseId: Int
    ): DetailPurchaseResponse

    // Update purchase
    @PUT("/purchases/{id}")
    suspend fun updatePurchase(
        @Header(Authorization) token: String,
        @Path("id") purchaseId: Int,
        @Body purchaseBody: UpdatePurchaseBody
    ): UpdatePurchaseResponse

    // Delete purchase
    @DELETE("/purchases/{id}")
    suspend fun deletePurchase(
        @Header(Authorization) token: String,
        @Path("id") purchaseId: Int
    ): MessageResponse

    // Purchase order
    @Headers("Content-Type: application/json")
    @PUT("/purchases/order")
    suspend fun putPurchasesOrder(
        @Header(Authorization) token: String,
        @Body ids: HashMap<String, List<Int>>
    ): MessageResponse

    // Change purchase stage
    @POST("/purchases/{id}/stage")
    suspend fun updatePurchaseStage(
        @Header(Authorization) token: String,
        @Path("id") purchaseId: Int,
        @Body changeStageBody: ChangeStageBody
    ):CreatePurchaseResponse

    // ********************************************************Questions******************************************
    @FormUrlEncoded
    @POST("/purchases/{purchaseId}/questions/{questionId}")
    suspend fun postStringAnswer(
        @Header(Authorization) token: String,
        @Path("purchaseId") purchaseId: Int,
        @Path("questionId") questionId: Int,
        @Field("text") text: String
    ):MessageResponse

    @FormUrlEncoded
    @POST("/purchases/{purchaseId}/questions/{questionId}")
    suspend fun postBooleanAnswer(
        @Header(Authorization) token: String,
        @Path("purchaseId") purchaseId: Int,
        @Path("questionId") questionId: Int,
        @Field("isTrue") isTrue: Boolean
    ):MessageResponse
}