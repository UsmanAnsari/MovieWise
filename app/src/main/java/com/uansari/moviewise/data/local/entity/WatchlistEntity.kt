package com.uansari.moviewise.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a movie saved to the user's watchlist.
 *
 * DIFFERENCE FROM MovieEntity:
 * MovieEntity  → temporary cache, replaced on refresh, user never controls it
 * WatchlistEntity → permanent, user explicitly adds/removes, survives app restarts
 *
 * addedAt lets us sort the watchlist by when the user saved each movie.
 */
@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    @ColumnInfo("poster_path") val posterPath: String?,
    @ColumnInfo("backdrop_path") val backdropPath: String?,
    @ColumnInfo("vote_average") val voteAverage: Double,
    @ColumnInfo("release_date") val releaseDate: String,
    @ColumnInfo("added_at") val addedAt: Long = System.currentTimeMillis()
)