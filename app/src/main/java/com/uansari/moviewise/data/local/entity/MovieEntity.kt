package com.uansari.moviewise.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a cached movie stored in Room.
 *
 * PURPOSE: Offline-first support for the Home screen.
 * When the app launches with no internet, Room returns
 * these cached records instead of showing an error.
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
    @ColumnInfo("is_popular") val isPopular: Boolean = true,
    @ColumnInfo("is_now_playing") val isNowPlaying: Boolean = true,
    @ColumnInfo("is_top_rated") val isTopRated: Boolean = true,
    @ColumnInfo("is_upcoming") val isUpcoming: Boolean = true,
    @ColumnInfo("cached_at") val cachedAt: Long = System.currentTimeMillis()
)