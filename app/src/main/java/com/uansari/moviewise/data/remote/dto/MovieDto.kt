package com.uansari.moviewise.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single movie object as returned by TMDB list endpoints.
 * e.g. /movie/popular, /movie/now_playing, /search/movie
 */
@Serializable
data class MovieDto(

    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("overview")
    val overview: String?,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("vote_average")
    val voteAverage: Double,

    @SerialName("vote_count")
    val voteCount: Int,

    @SerialName("release_date")
    val releaseDate: String?,

    @SerialName("genre_ids")
    val genreIds: List<Int>,

    @SerialName("popularity")
    val popularity: Double,

    @SerialName("adult")
    val adult: Boolean = false,

    @SerialName("original_language")
    val originalLanguage: String?
)