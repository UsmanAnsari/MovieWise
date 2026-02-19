package com.uansari.moviewise.util

import com.uansari.moviewise.domain.model.CastMember
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.model.MovieDetail

/**
 * Centralized test data for consistent testing across all test files.
 * Reduces duplication and makes tests more readable.
 */
object TestData {

    fun createMovie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test overview",
        posterPath: String? = "/test.jpg",
        backdropPath: String? = "/backdrop.jpg",
        voteAverage: Double = 7.5,
        releaseDate: String = "2024-01-01",
    ) = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
    )

    fun createMovieDetail(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test overview",
        posterPath: String? = "/test.jpg",
        backdropPath: String? = "/backdrop.jpg",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000,
        releaseDate: String = "2024-01-01",
        runtime: Int = 120,
        status: String = "Released",
        tagline: String = "Test tagline",
        genres: List<String> = listOf("Action", "Drama"),
        cast: List<CastMember> = emptyList()
    ) = MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        runtime = runtime,
        status = status,
        tagline = tagline,
        genres = genres,
        cast = cast
    )

    fun createCastMember(
        id: Int = 1,
        name: String = "Test Actor",
        character: String = "Test Character",
        profilePath: String? = "/actor.jpg"
    ) = CastMember(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath
    )

    val sampleMovies = listOf(
        createMovie(id = 1, title = "Movie 1"),
        createMovie(id = 2, title = "Movie 2"),
        createMovie(id = 3, title = "Movie 3")
    )
}