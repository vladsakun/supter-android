package com.supter.data.db.entity

import androidx.room.Entity

@Entity(tableName = "users")
data class UserEntity(
    val id: Int,
    val accessToken: String,
    val name: String,
    val email: String,
)