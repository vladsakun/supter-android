package com.supter.data.network

import com.supter.data.response.ErrorResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.ResponseBody
import retrofit2.HttpException

interface BaseNetworkDataSource {

    suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T>
    fun convertErrorBody(throwable: HttpException):ErrorResponse?
    fun convertErrorResponseBody(errorResponseBody: ResponseBody?):ErrorResponse?
}