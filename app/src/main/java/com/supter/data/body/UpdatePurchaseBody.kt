package com.supter.data.body

data class UpdatePurchaseBody(
    val title:String,
    val price:Double,
    val description:String?,
    val link:String?
)