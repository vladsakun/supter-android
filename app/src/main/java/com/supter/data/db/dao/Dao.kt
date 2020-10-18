package com.supter.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.supter.data.db.entity.PurchaseEntity

@Dao
interface Dao {

    //Purchase

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(purchaseEntity: List<PurchaseEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOneItem(purchaseEntity: PurchaseEntity)

    @Query("SELECT * FROM supter_db")
    fun getListOfMovies(): LiveData<List<PurchaseEntity>>
}