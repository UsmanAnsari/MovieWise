package com.uansari.moviewise.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uansari.moviewise.domain.util.MovieCategory

/**
 * Represents a cached movie stored in Room.
 *
 * PURPOSE: Offline-first support for the Home screen.
 * When the app launches with no internet, Room returns
 * these cached records instead of showing an error.
 *
 * CATEGORY FIELD:
 * The same movie can appear in multiple lists (Popular AND Top Rated).
 * The category field ("POPULAR", "NOW_PLAYING", etc.) lets us query
 * "give me all movies in the 'popular' category" from the movie table.
 *
 * Cache is cleared and replaced on every successful API refresh.
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    @ColumnInfo("poster_path") val posterPath: String?,
    @ColumnInfo("backdrop_path") val backdropPath: String?,
    @ColumnInfo("vote_average") val voteAverage: Double,
    @ColumnInfo("release_date") val releaseDate: String,
    val category: MovieCategory,
    @ColumnInfo("cached_at") val cachedAt: Long = System.currentTimeMillis()
)