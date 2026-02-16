package com.uansari.moviewise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uansari.moviewise.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    /**
     * Returns cached movies for a given category as a Flow.
     */
    @Query("SELECT * FROM movies WHERE category = :category ORDER BY vote_average DESC")
    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>>

    /**
     * Inserts or replaces movies.
     * REPLACE strategy means if a movie already exists with the same ID,
     * it gets overwritten with fresh data from the API.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    /**
     * Clears all cached movies for a category before inserting fresh data.
     * Called before insertMovies() during a refresh to avoid stale entries.
     */
    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun deleteMoviesByCategory(category: String)

    /**
     * Returns a single cached movie by ID — used to check if
     * a detail screen can load from cache before hitting the API.
     */
    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieById(movieId: Int): MovieEntity?
}