package com.supter.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.supter.data.db.dao.Dao
import com.supter.data.db.entity.PurchaseEntity


@Database(
    entities = [PurchaseEntity::class],
    version = 1
)

//Database
abstract class PurchaseDatabase : RoomDatabase() {

    abstract fun movieDao(): Dao

    companion object {
        @Volatile
        private var instance: PurchaseDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDB(context).also { instance = it }
        }

        private fun buildDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PurchaseDatabase::class.java, "supter.db"
            )
                .build()

    }

}