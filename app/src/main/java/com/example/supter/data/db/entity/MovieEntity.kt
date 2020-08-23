package com.example.supter.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movie_db")
class MovieEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Double,

    val popularity: Double?,
    val adult: Boolean?,
    val original_title: String?,
    var title: String,
    var overview: String?,
    var release_date: String?,
    val poster_path: String?,
    val vote_average: Double?,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?

) : Serializable


