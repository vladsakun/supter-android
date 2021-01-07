package com.supter.data.response.purchase

data class CreatePurchaseResponse(
	val data: PurchaseData
)

data class UserData(
	val period: Number,
	val name: String,
	val id: Int,
	val savings: String,
	val incomeRemainder: String,
	val email: String
)

