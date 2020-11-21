package com.supter.data.network

import com.google.gson.Gson
import com.supter.data.response.ErrorResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

open class BaseNetworkDataSourceImpl : BaseNetworkDataSource {

    override suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                val result = apiCall.invoke()

                if (result is Response<*> && !result.isSuccessful) {
                    val code = result.code()
                    val errorResponse = convertErrorResponseBody(result.errorBody())
                    ResultWrapper.GenericError(code, errorResponse)

                } else {
                    ResultWrapper.Success(apiCall.invoke())
                }

            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> return@withContext ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    override fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            val gson = Gson()
            return gson.fromJson(
                throwable.response()?.errorBody()?.charStream()?.readText(),
                ErrorResponse::class.java
            )
        } catch (exception: Exception) {
            null
        }
    }

    override fun convertErrorResponseBody(errorResponseBody: ResponseBody?): ErrorResponse? {
        return try {
            val gson = Gson()
            return gson.fromJson(
                errorResponseBody?.charStream()?.readText(),
                ErrorResponse::class.java
            )
        } catch (exception: Exception) {
            null
        }
    }
}