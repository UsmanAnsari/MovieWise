package com.uansari.moviewise.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uansari.moviewise.data.local.dao.MovieDao
import com.uansari.moviewise.data.local.dao.WatchlistDao
import com.uansari.moviewise.data.local.entity.MovieEntity
import com.uansari.moviewise.data.local.entity.WatchlistEntity

@Database(
    entities = [MovieEntity::class, WatchlistEntity::class],

    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        const val DATABASE_NAME = "movie_wise_db"
    }
}