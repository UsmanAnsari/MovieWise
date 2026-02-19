package com.uansari.moviewise.data.mappers

import com.uansari.moviewise.data.remote.dto.CastMemberDto
import com.uansari.moviewise.data.remote.dto.CreditsDto
import com.uansari.moviewise.data.remote.dto.GenreDto
import com.uansari.moviewise.data.remote.dto.MovieDetailDto
import com.uansari.moviewise.data.remote.dto.MovieDto
import com.uansari.moviewise.domain.util.MovieCategory
import com.uansari.moviewise.util.Constants
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MovieDtoMapperTest {

    @Test
    fun `MovieDto toDomain maps all fields correctly`() {
        // Given
        val dto = MovieDto(
            id = 1,
            title = "Test Movie",
            overview = "Test overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 7.5,
            voteCount = 1000,
            releaseDate = "2024-01-01",
            genreIds = listOf(28, 12),
            popularity = 100.0,
            adult = false,
            originalLanguage = "en"
        )

        // When
        val movie = dto.toDomain()

        // Then
        assertEquals(1, movie.id)
        assertEquals("Test Movie", movie.title)
        assertEquals("Test overview", movie.overview)
        assertEquals(
            "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_POSTER}/poster.jpg", movie.posterPath
        )
        assertEquals(
            "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_BACKDROP}/backdrop.jpg",
            movie.backdropPath
        )
        assertEquals(7.5, movie.voteAverage, 0.01)
        assertEquals("2024-01-01", movie.releaseDate)
    }

    @Test
    fun `MovieDto toEntity maps all fields correctly`() {
        // Given
        val dto = MovieDto(
            id = 1,
            title = "Test Movie",
            overview = "Test overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 7.5,
            voteCount = 1000,
            releaseDate = "2024-01-01",
            genreIds = listOf(28),
            popularity = 100.0,
            adult = false,
            originalLanguage = "en"
        )

        // When
        val entity = dto.toEntity(MovieCategory.POPULAR)

        // Then
        assertEquals(1, entity.id)
        assertEquals("Test Movie", entity.title)
        assertEquals("Test overview", entity.overview)
        assertEquals("/poster.jpg", entity.posterPath) // Stored as path, not full URL
        assertEquals("/backdrop.jpg", entity.backdropPath)
        assertEquals(7.5, entity.voteAverage, 0.01)
        assertEquals("2024-01-01", entity.releaseDate)
    }

    @Test
    fun `MovieDetailDto toDomain maps all fields including cast`() {
        // Given
        val castDto = CastMemberDto(
            id = 1,
            name = "Test Actor",
            character = "Test Character",
            profilePath = "/actor.jpg",
            order = 1
        )
        val creditsDto = CreditsDto(cast = listOf(castDto))
        val genreDto = GenreDto(id = 28, name = "Action")

        val detailDto = MovieDetailDto(
            id = 1,
            title = "Test Movie",
            overview = "Test overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 7.5,
            voteCount = 1000,
            releaseDate = "2024-01-01",
            runtime = 120,
            status = "Released",
            tagline = "Test tagline",
            genres = listOf(genreDto),
            credits = creditsDto
        )

        // When
        val movieDetail = detailDto.toDomain()

        // Then
        assertEquals(1, movieDetail.id)
        assertEquals("Test Movie", movieDetail.title)
        assertEquals(120, movieDetail.runtime)
        assertEquals("Released", movieDetail.status)
        assertEquals("Test tagline", movieDetail.tagline)
        assertEquals(listOf("Action"), movieDetail.genres)
        assertEquals(1, movieDetail.cast.size)
        assertEquals("Test Actor", movieDetail.cast.first().name)
        assertEquals("Test Character", movieDetail.cast.first().character)
    }

    @Test
    fun `CastMemberDto toDomain builds full URL for profile path`() {
        // Given
        val castDto = CastMemberDto(
            id = 1,
            name = "Test Actor",
            character = "Test Character",
            profilePath = "/actor.jpg",
            order = 1
        )

        // When
        val castMember = castDto.toDomain()

        // Then
        assertEquals(
            "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_PROFILE}/actor.jpg",
            castMember.profilePath
        )
    }
}
