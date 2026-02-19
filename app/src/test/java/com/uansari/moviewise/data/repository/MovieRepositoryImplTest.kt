package com.uansari.moviewise.data.repository

import app.cash.turbine.test
import com.uansari.moviewise.data.local.dao.MovieDao
import com.uansari.moviewise.data.local.dao.WatchlistDao
import com.uansari.moviewise.data.local.entity.WatchlistEntity
import com.uansari.moviewise.data.remote.api.TmdbApiService
import com.uansari.moviewise.util.TestData
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private lateinit var apiService: TmdbApiService
    private lateinit var movieDao: MovieDao
    private lateinit var watchlistDao: WatchlistDao
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        movieDao = mockk(relaxed = true)
        watchlistDao = mockk(relaxed = true)

        repository = MovieRepositoryImpl(apiService, movieDao, watchlistDao)
    }

    // Watchlist Tests

    @Test
    fun `getWatchlist returns movies from Room`() = runTest {
        // Given
        val watchlistEntity = WatchlistEntity(
            id = 1,
            title = "Watchlist Movie",
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = null,
            voteAverage = 8.0,
            releaseDate = "2024-01-01",
            addedAt = System.currentTimeMillis()
        )

        every { watchlistDao.getWatchlist() } returns flowOf(listOf(watchlistEntity))

        // When
        repository.getWatchlist().test {
            // Then
            val movies = awaitItem()
            assertEquals(1, movies.size)
            assertEquals("Watchlist Movie", movies.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addToWatchlist calls DAO`() = runTest {
        // Given
        val movie = TestData.createMovie()

        // When
        repository.addToWatchlist(movie)

        // Then
        coVerify { watchlistDao.addToWatchlist(any()) }
    }

    @Test
    fun `removeFromWatchlist calls DAO with correct id`() = runTest {
        // Given
        val movieId = 123

        // When
        repository.removeFromWatchlist(movieId)

        // Then
        coVerify { watchlistDao.removeFromWatchlist(movieId) }
    }

    @Test
    fun `isMovieInWatchlist returns correct status`() = runTest {
        // Given
        val movieId = 123
        every { watchlistDao.isInWatchlist(movieId) } returns flowOf(true)

        // When
        repository.isMovieInWatchlist(movieId).test {
            // Then
            val isInWatchlist = awaitItem()
            assertTrue(isInWatchlist)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
