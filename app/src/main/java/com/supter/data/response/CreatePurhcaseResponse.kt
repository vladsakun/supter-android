package com.supter.data.response

data class CreatePurchaseResponse(
	val data: PurchaseData
)

data class PurchaseData(
	val id: Int,
	val title: String,
	val price: Double,
	val description: String?,
	val stage: String,
	val user: UserData,
	val potential: Int,
	val order: Int,
	val thinkingTime:String

)

data class UserData(
	val period: Int,
	val name: String,
	val id: Int,
	val savings: String,
	val incomeRemainder: String,
	val email: String
)

