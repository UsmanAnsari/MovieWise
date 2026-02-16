package com.uansari.moviewise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uansari.moviewise.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    /**
     * Returns the full watchlist sorted by most recently added.
     * Flow means the Watchlist screen updates instantly when
     * the user adds or removes a movie from the detail screen.
     */
    @Query("SELECT * FROM watchlist ORDER BY added_at DESC")
    fun getWatchlist(): Flow<List<WatchlistEntity>>

    /**
     * Adds a movie to the watchlist.
     * IGNORE strategy means tapping "Add to Watchlist" twice
     * on the same movie does nothing — no duplicates.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToWatchlist(movie: WatchlistEntity)

    /**
     * Removes a movie from the watchlist by its ID.
     */
    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun removeFromWatchlist(movieId: Int)

    /**
     * Checks if a specific movie is already in the watchlist.
     * Used on the Detail screen to show the correct button state
     * ("Add to Watchlist" vs "Remove from Watchlist").
     */
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    fun isInWatchlist(movieId: Int): Flow<Boolean>
}

