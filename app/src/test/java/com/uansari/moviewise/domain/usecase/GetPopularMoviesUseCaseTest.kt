package com.uansari.moviewise.domain.usecase

import app.cash.turbine.test
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.repository.MovieRepository
import com.uansari.moviewise.util.TestData
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetPopularMoviesUseCaseTest {

    @Test
    fun `invoke calls repository getPopularMovies`() = runTest {
        // Given
        val repository = mockk<MovieRepository>()
        val movies = TestData.sampleMovies
        every { repository.getPopularMovies() } returns 
            flowOf(Resource.Success(movies))

        val useCase = GetPopularMoviesUseCase(repository)

        // When
        useCase().test {
            // Then
            val result = awaitItem()
            assertEquals(Resource.Success(movies), result)
            awaitComplete()
        }

        coVerify { repository.getPopularMovies() }
    }
}
