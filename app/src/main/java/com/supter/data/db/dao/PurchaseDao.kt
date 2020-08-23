package com.supter.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.supter.data.db.entity.PurchaseEntity

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(movieEntity: List<PurchaseEntity>)

    @Query("SELECT * FROM purchase_db")
    fun getListOfMovies(): LiveData<List<PurchaseEntity>>
}