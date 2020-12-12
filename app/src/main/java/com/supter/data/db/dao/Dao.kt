package com.supter.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(purchaseEntity: List<PurchaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertOneItem(purchaseEntity: PurchaseEntity)

    @Query("SELECT * FROM purchase")
    fun getPurchaseFlowList(): Flow<List<PurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertUser(userEntity: UserEntity)

    @Query("SELECT * FROM users")
    fun getUserFlow(): Flow<UserEntity?>
}