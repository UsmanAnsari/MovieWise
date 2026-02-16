package com.uansari.moviewise.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Full movie detail returned by /movie/{movie_id}?append_to_response=credits
 *
 * Note: We append "credits" to get cast in a single request
 * rather than making two separate API calls.
 */
@Serializable
data class MovieDetailDto(

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

    @SerialName("runtime")
    val runtime: Int?,

    @SerialName("status")
    val status: String?,

    @SerialName("tagline")
    val tagline: String?,

    @SerialName("genres")
    val genres: List<GenreDto>,

    @SerialName("credits")
    val credits: CreditsDto?
)

@Serializable
data class GenreDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String
)

@Serializable
data class CreditsDto(
    @SerialName("cast")
    val cast: List<CastMemberDto>
)

@Serializable
data class CastMemberDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("character")
    val character: String?,

    @SerialName("profile_path")
    val profilePath: String?,

    @SerialName("order")
    val order: Int
)