package com.supter.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "purchase")
data class PurchaseEntity(

    val name:String,
    val cost:Double,
    val priority:Int,
    var status:String,
    val questionsJSON:String?,
    val remind: Double,
    val realPeriod:Double,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?

) : Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


