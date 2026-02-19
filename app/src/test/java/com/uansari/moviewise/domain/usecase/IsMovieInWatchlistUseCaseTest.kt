package com.uansari.moviewise.domain.usecase

import app.cash.turbine.test
import com.uansari.moviewise.domain.repository.MovieRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class IsMovieInWatchlistUseCaseTest {

    @Test
    fun `invoke returns correct watchlist status`() = runTest {
        // Given
        val repository = mockk<MovieRepository>()
        val movieId = 123
        every { repository.isMovieInWatchlist(movieId) } returns flowOf(true)

        val useCase = IsMovieInWatchlistUseCase(repository)

        // When
        useCase(movieId).test {
            // Then
            val isInWatchlist = awaitItem()
            assertEquals(true, isInWatchlist)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.isMovieInWatchlist(movieId) }
    }
}
