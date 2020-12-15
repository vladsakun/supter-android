package com.supter.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

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