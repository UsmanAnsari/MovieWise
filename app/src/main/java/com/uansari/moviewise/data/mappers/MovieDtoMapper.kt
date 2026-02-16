package com.uansari.moviewise.data.mappers

import com.uansari.moviewise.data.local.entity.MovieEntity
import com.uansari.moviewise.data.remote.dto.CastMemberDto
import com.uansari.moviewise.data.remote.dto.MovieDetailDto
import com.uansari.moviewise.data.remote.dto.MovieDto
import com.uansari.moviewise.domain.model.CastMember
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.util.MovieCategory
import com.uansari.moviewise.domain.model.MovieDetail
import com.uansari.moviewise.util.Constants

/**
 * Converts a MovieDto (API response) to a Movie domain model.
 * category is passed in because the DTO doesn't know which list it came from.
 */
fun MovieDto.toDomain(category: MovieCategory? = null): Movie = Movie(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterPath = Constants.posterUrl(posterPath),
    backdropPath = Constants.backdropUrl(backdropPath),
    voteAverage = voteAverage,
    releaseDate = releaseDate.orEmpty(),
    category = category
)

/**
 * Converts a MovieDetailDto to a MovieDetail domain model.
 */
fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterPath = Constants.posterUrl(posterPath),
    backdropPath = Constants.backdropUrl(backdropPath),
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate.orEmpty(),
    runtime = runtime ?: 0,
    status = status.orEmpty(),
    tagline = tagline.orEmpty(),
    genres = genres.map { it.name },
    cast = credits?.cast?.sortedBy { it.order }
        ?.take(15)                 // Top 15 cast members
        ?.map { it.toDomain() } ?: emptyList())

fun CastMemberDto.toDomain(): CastMember = CastMember(
    id = id,
    name = name,
    character = character.orEmpty(),
    profilePath = Constants.profileUrl(profilePath)
)


/**
 * Converts a MovieDto to a MovieEntity for Room storage.
 * category tells the DB which list this movie belongs to.
 */
fun MovieDto.toEntity(category: MovieCategory): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate.orEmpty(),
    category = category
)