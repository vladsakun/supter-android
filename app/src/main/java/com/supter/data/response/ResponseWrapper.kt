package com.supter.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseWrapper<T> : Serializable {
    @SerializedName("user")
    val data: T? = null
    @SerializedName("error")
    val error: Error? = null
}