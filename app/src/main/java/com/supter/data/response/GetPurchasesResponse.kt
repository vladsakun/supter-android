package com.supter.data.response

data class GetPurchasesResponse(
	val data: List<PurchaseData>
)

data class DataItem(
	val stage: String,
	val price: String,
	val description: String?,
	val id: Int,
	val title: String,
	val potential: Int,
	val userId: Int,
	val order: Int,
	val thinkingTime:String
)

