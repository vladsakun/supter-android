package com.supter.data.response

data class AccountResponse(
	val data: Data
)

data class Data(
	val id: Int,
	val name: String,
	val email: String,
	val incomeRemainder: Double,
	val savings: Double,
	val period: Double
)

