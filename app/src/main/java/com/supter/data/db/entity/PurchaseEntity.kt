package com.supter.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "supter_db")
data class PurchaseEntity(

    val priority:Int,
    val status:Int,
    val cost:Double,
    val name:String,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?

) : Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


