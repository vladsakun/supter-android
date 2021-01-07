package com.supter.data.response.account

data class AccountResponse(
    val data: Account
)

data class Account(
    val id: Int,
    val name: String?,
    val email: String,
    val incomeRemainder: String?,
    val period: Float?,
    val balance: String?,
    val salaryDay: Int
)

