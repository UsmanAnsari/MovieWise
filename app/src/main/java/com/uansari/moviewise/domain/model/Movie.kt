package com.uansari.moviewise.domain.model

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
    val isPopular: Boolean = false,
    val isNowPlaying: Boolean = false,
    val isTopRated: Boolean = false,
    val isUpcoming: Boolean = false,
)