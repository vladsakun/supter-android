package com.supter.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "purchase")
data class PurchaseEntity(

    @PrimaryKey(autoGenerate = false)
    var id: Int,

    var title: String,
    var price: Double,
    var order: Int,
    var stage: String,
    var potential: Int,
    var description: String?,
    var questionsJSON: String?,
    var remind: Double,
    var realPeriod: Int,
    val thinkingTime:String,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray?

): Serializable




