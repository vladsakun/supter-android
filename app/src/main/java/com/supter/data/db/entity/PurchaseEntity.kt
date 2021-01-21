package com.supter.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.supter.data.db.DataConverter
import com.supter.data.response.purchase.QuestionsItem
import java.io.Serializable

@Entity(tableName = "purchase")
data class PurchaseEntity(

    @PrimaryKey(autoGenerate = false)
    var id: Int,

    var title: String,
    var price: Double,
    var order: Int,
    var stage: String,
    var potential: Float,
    var description: String?,
    var remind: Double,
    var realPeriod: Int,
    val thinkingTime:String,
    val createdAt:String,
    var link:String?,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray?

): Serializable




