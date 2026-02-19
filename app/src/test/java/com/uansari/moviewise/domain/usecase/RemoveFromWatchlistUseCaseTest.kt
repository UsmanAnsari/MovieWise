package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.repository.MovieRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RemoveFromWatchlistUseCaseTest {

    @Test
    fun `invoke calls repository with correct movieId`() = runTest {
        // Given
        val repository = mockk<MovieRepository>(relaxed = true)
        val movieId = 123
        val useCase = RemoveFromWatchlistUseCase(repository)

        // When
        useCase(movieId)

        // Then
        coVerify { repository.removeFromWatchlist(movieId) }
    }
}
