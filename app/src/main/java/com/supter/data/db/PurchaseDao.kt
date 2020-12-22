package com.supter.data.db

import androidx.room.*
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(purchaseEntity: List<PurchaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOneItem(purchaseEntity: PurchaseEntity)

    @Delete
    suspend fun deletePurchaseEntity(purchaseEntity: PurchaseEntity)

    @Query("SELECT * FROM purchase")
    fun getPurchaseFlowList(): Flow<List<PurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(userEntity: UserEntity)

    @Query("SELECT * FROM users")
    fun getUserFlow(): Flow<UserEntity>

    @Query("DELETE FROM users")
    suspend fun clearUserTable()

    @Query("DELETE FROM purchase")
    suspend fun clearPurchaseTable()
}