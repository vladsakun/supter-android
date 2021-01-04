package com.supter.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val name: String?,
    val email: String,
    val incomeRemainder:Double?,
    val balance: Double?,
    val period:Double?,
    val salaryDate:Int = 1,
)