package com.uansari.moviewise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.uansari.moviewise.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    /**
     * Returns cached movies for a given category as a Flow.
     */
    @Query("SELECT * FROM movies WHERE is_now_playing = 1 ORDER BY vote_average DESC")
    fun getMoviesByCategoryNowPlaying(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_popular = 1 ORDER BY vote_average DESC")
    fun getMoviesByCategoryPopular(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_top_rated = 1 ORDER BY vote_average DESC")
    fun getMoviesByCategoryTopRated(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_upcoming = 1 ORDER BY vote_average DESC")
    fun getMoviesByCategoryUpcoming(): Flow<List<MovieEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOnlyNew(movies: List<MovieEntity>): List<Long>

    @Update
    suspend fun updateExisting(movie: MovieEntity)

    /**
     * Inserts or update movies.
     * Update strategy means if a movie already exists with the same ID in other `MovieCategory [Popular,Top_Rated etc.]`,
     * it gets updated with the other category flag while retaining the previous one.
     */
    @Transaction
    suspend fun upsertMovies(movies: List<MovieEntity>) {
        val insertResults = insertOnlyNew(movies)

        for (i in insertResults.indices) {
            // If result is -1, the movie already existed (conflict occurred)
            if (insertResults[i] == -1L) {
                val newMovie = movies[i]
                val existingMovie = getMovieById(newMovie.id)

                if (existingMovie != null) {
                    // Retain 'true' from both old and new versions
                    val mergedMovie = existingMovie.copy(
                        isPopular = existingMovie.isPopular || newMovie.isPopular,
                        isNowPlaying = existingMovie.isNowPlaying || newMovie.isNowPlaying,
                        isTopRated = existingMovie.isTopRated || newMovie.isTopRated,
                        isUpcoming = existingMovie.isUpcoming || newMovie.isUpcoming
                    )
                    updateExisting(mergedMovie)
                }
            }
        }
    }


    /**
     * Clears all cached movies for a category before inserting fresh data.
     * Called before insertMovies() during a refresh to avoid stale entries.
     */
    @Query("DELETE FROM movies WHERE is_now_playing = 1 AND is_popular = 0 AND is_top_rated = 0 AND is_upcoming = 0 ")
    suspend fun deleteMoviesByCategoryNowPlaying()

    @Query("DELETE FROM movies WHERE is_popular = 1 AND is_now_playing = 0 AND is_top_rated = 0 AND is_upcoming = 0 ")
    suspend fun deleteMoviesByCategoryPopular()

    @Query("DELETE FROM movies WHERE is_top_rated = 1 AND is_now_playing = 0 AND is_popular = 0 AND is_upcoming = 0 ")
    suspend fun deleteMoviesByCategoryTopRated()

    @Query("DELETE FROM movies WHERE is_upcoming = 1 AND is_now_playing = 0 AND is_popular = 0 AND is_top_rated = 0 ")
    suspend fun deleteMoviesByCategoryUpcoming()

    /**
     * Returns a single cached movie by ID — used to check if
     * a detail screen can load from cache before hitting the API.
     */
    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieById(movieId: Int): MovieEntity?
}