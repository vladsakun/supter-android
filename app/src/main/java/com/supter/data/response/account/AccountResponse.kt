package com.supter.data.response.account

data class AccountResponse(
	val data: Data
)

data class Data(
	val id: Int,
	val name: String?,
	val email: String,
	val incomeRemainder: Double?,
	val period: Double?,
	val savings: Double?,
)

