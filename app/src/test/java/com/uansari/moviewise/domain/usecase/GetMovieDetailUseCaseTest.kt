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

class GetMovieDetailUseCaseTest {

    @Test
    fun `invoke calls repository with correct movieId`() = runTest {
        // Given
        val repository = mockk<MovieRepository>()
        val movieDetail = TestData.createMovieDetail(id = 123)
        val movieId = 123

        every { repository.getMovieDetail(movieId) } returns 
            flowOf(Resource.Success(movieDetail))

        val useCase = GetMovieDetailUseCase(repository)

        // When
        useCase(movieId).test {
            // Then
            val result = awaitItem()
            assertEquals(movieDetail, (result as Resource.Success).data)
            awaitComplete()
        }

        coVerify { repository.getMovieDetail(movieId) }
    }
}
