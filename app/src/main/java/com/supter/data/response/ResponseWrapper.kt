package com.supter.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.Error

class ResponseWrapper<T> : Serializable {
    @SerializedName("user")
    val data: T? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("error")
    val error: Error? = null
}