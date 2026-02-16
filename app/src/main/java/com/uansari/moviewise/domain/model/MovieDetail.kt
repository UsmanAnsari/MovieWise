package com.uansari.moviewise.domain.model

/**
 * Full movie detail — used exclusively on the Detail screen.
 */
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val runtime: Int,             // in minutes — e.g. 148
    val status: String,           // "Released", "In Production", etc.
    val tagline: String,
    val genres: List<String>,
    val cast: List<CastMember>
)

/**
 * Represents a single cast member shown on the Detail screen.
 * profilePath is a full URL ready for Coil — built by the mapper.
 */
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)