package com.uansari.moviewise.domain.usecase

import app.cash.turbine.test
import com.uansari.moviewise.domain.repository.MovieRepository
import com.uansari.moviewise.util.TestData
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetWatchlistUseCaseTest {

    @Test
    fun `invoke returns watchlist from repository`() = runTest {
        // Given
        val repository = mockk<MovieRepository>()
        val movies = TestData.sampleMovies
        every { repository.getWatchlist() } returns flowOf(movies)

        val useCase = GetWatchlistUseCase(repository)

        // When
        useCase().test {
            // Then
            val result = awaitItem()
            assertEquals(movies, result)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.getWatchlist() }
    }
}
