package com.uansari.moviewise.domain.model

import com.uansari.moviewise.domain.util.MovieCategory

/**
 * This is what ViewModels, UseCases, and the UI all work with.
 * No @Serializable. No @Entity. No framework annotations of any kind.
 *
 * posterPath and backdropPath are FULL URLs here (built by the mapper).
 * The UI can use them directly with Coil — no URL construction needed.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val category: MovieCategory? // Could be null in case for WatchList - Category is only useful for HomeScreen
)