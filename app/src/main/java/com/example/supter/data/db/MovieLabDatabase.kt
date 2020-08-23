package com.example.supter.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.supter.data.db.dao.MovieDao
import com.example.supter.data.db.entity.MovieEntity


@Database(
    entities = [MovieEntity::class],
    version = 1
)

//Database
abstract class MovieLabDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var instance: MovieLabDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDB(context).also { instance = it }
        }

        private fun buildDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MovieLabDatabase::class.java, "movie.db"
            )
                .build()

    }

}