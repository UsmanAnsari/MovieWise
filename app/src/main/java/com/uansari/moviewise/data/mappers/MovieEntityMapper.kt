package com.uansari.moviewise.data.mappers

import com.uansari.moviewise.data.local.entity.MovieEntity
import com.uansari.moviewise.data.local.entity.WatchlistEntity
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.util.Constants

/**
 * Converts a cached MovieEntity back to a Movie domain model.
 * We build the full image URL here because we stored only the raw path.
 */
fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = Constants.posterUrl(posterPath),
    backdropPath = Constants.backdropUrl(backdropPath),
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    isNowPlaying = isNowPlaying,
    isPopular = isPopular,
    isTopRated = isTopRated,
    isUpcoming = isUpcoming,
)

/**
 * Converts a WatchlistEntity to a Movie domain model.
 */
fun WatchlistEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = Constants.posterUrl(posterPath),
    backdropPath = Constants.backdropUrl(backdropPath),
    voteAverage = voteAverage,
    releaseDate = releaseDate,
)


/**
 * Converts a Movie domain model to a WatchlistEntity for saving.
 * We strip the full URL back to just the path for storage efficiency.
 */
fun Movie.toWatchlistEntity(): WatchlistEntity = WatchlistEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate
)