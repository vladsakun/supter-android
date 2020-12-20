package com.supter.data.response

data class UpdatePurchaseResponse(
	val data: UpdatePurchaseData
)

data class UpdatePurchaseData(
	val stage: String,
	val price: Int,
	val description: String?,
	val id: Int,
	val title: String,
	val potential: Int,
	val userId: Int,
	val order: Int
)

