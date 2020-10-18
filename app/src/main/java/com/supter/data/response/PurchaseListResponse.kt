package com.supter.data.response

import com.google.gson.annotations.SerializedName

data class PurchaseListResponse(
    @SerializedName("results") val results: List<PurchaseDetailResponse>
)