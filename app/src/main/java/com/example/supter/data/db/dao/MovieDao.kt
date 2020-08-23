package com.example.supter.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.supter.data.db.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(movieEntity: List<MovieEntity>)

    @Query("SELECT * FROM movie_db")
    fun getListOfMovies(): LiveData<List<MovieEntity>>
}