package com.supter.data.response.purchase

import com.supter.data.response.purchase.PurchaseData

data class UpdatePurchaseResponse(
	val data: PurchaseData
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

