package com.supter.data.network

import com.supter.data.body.*
import com.supter.data.response.*
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.purchase.*
import com.supter.utils.Authorization
import okhttp3.MultipartBody
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
        @Body account: AccountBody
    ): AccountResponse

    //Read user
    @GET("account")
    suspend fun fetchUser(
    ): AccountResponse

    // *************************************************Purchase*********************************************

    // Create purchase
    @POST("purchases")
    suspend fun createPurchase(
        @Body purchaseBody: PurchaseBody
    ): CreatePurchaseResponse

    // Get purchases
    @GET("purchases")
    suspend fun getPurchasesList(
    ): GetPurchasesResponse

    // Get purchase by id
    @GET("purchases/{id}")
    suspend fun getPurchase(
        @Path("id") purchaseId: Int
    ): DetailPurchaseResponse

    // Update purchase
    @PUT("/purchases/{id}")
    suspend fun updatePurchase(
        @Path("id") purchaseId: Int,
        @Body purchaseBody: UpdatePurchaseBody
    ): UpdatePurchaseResponse

    // Delete purchase
    @DELETE("purchases/{id}")
    suspend fun deletePurchase(
        @Path("id") purchaseId: Int
    ): MessageResponse

    // Purchase order
    @Headers("Content-Type: application/json")
    @PUT("purchases/order")
    suspend fun putPurchasesOrder(
        @Body ids: HashMap<String, Any>,
    ): MessageResponse

    // Change purchase stage
    @POST("purchases/{id}/stage")
    suspend fun updatePurchaseStage(
        @Path("id") purchaseId: Int,
        @Body changeStageBody: ChangeStageBody
    ):CreatePurchaseResponse

    @Multipart
    @POST("purchases/{purchase_id}/image")
    suspend fun postPurchaseImage(
        @Path("purchase_id") purchaseId: Int,
        @Part image:MultipartBody.Part
    ):PurchaseData

    // ********************************************************Questions******************************************
    @FormUrlEncoded
    @POST("purchases/{purchaseId}/questions/{questionId}")
    suspend fun postStringAnswer(
        @Path("purchaseId") purchaseId: Int,
        @Path("questionId") questionId: Int,
        @Field("text") text: String
    ): AnswerQuestionResponse

    @FormUrlEncoded
    @POST("purchases/{purchaseId}/questions/{questionId}")
    suspend fun postBooleanAnswer(
        @Path("purchaseId") purchaseId: Int,
        @Path("questionId") questionId: Int,
        @Field("isTrue") isTrue: Boolean
    ):AnswerQuestionResponse
}