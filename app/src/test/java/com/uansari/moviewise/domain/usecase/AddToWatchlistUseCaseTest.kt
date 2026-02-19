package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.repository.MovieRepository
import com.uansari.moviewise.util.TestData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToWatchlistUseCaseTest {

    @Test
    fun `invoke calls repository addToWatchlist`() = runTest {
        // Given
        val repository = mockk<MovieRepository>(relaxed = true)
        val movie = TestData.createMovie()
        val useCase = AddToWatchlistUseCase(repository)

        // When
        useCase(movie)

        // Then
        coVerify { repository.addToWatchlist(movie) }
    }
}
