package com.uansari.moviewise.di

import android.content.Context
import androidx.room.Room
import com.uansari.moviewise.data.local.dao.MovieDao
import com.uansari.moviewise.data.local.dao.WatchlistDao
import com.uansari.moviewise.data.local.database.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        MovieDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao =
        database.movieDao()

    @Provides
    @Singleton
    fun provideWatchlistDao(database: MovieDatabase): WatchlistDao =
        database.watchlistDao()
}