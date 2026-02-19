package com.uansari.moviewise.ui.home

import app.cash.turbine.test
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.usecase.GetNowPlayingMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetPopularMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetTopRatedMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetUpcomingMoviesUseCase
import com.uansari.moviewise.util.MainDispatcherRule
import com.uansari.moviewise.util.TestData
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocked dependencies
    private lateinit var getPopularMovies: GetPopularMoviesUseCase
    private lateinit var getNowPlayingMovies: GetNowPlayingMoviesUseCase
    private lateinit var getTopRatedMovies: GetTopRatedMoviesUseCase
    private lateinit var getUpcomingMovies: GetUpcomingMoviesUseCase

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        getPopularMovies = mockk()
        getNowPlayingMovies = mockk()
        getTopRatedMovies = mockk()
        getUpcomingMovies = mockk()
    }

    @Test
    fun `initial state is loading`() {
        // Given - all use cases return loading
        coEvery { getPopularMovies() } returns flowOf(Resource.Loading())
        coEvery { getNowPlayingMovies() } returns flowOf(Resource.Loading())
        coEvery { getTopRatedMovies() } returns flowOf(Resource.Loading())
        coEvery { getUpcomingMovies() } returns flowOf(Resource.Loading())

        // When - ViewModel is created (init block runs)
        viewModel = HomeViewModel(
            getPopularMovies,
            getNowPlayingMovies,
            getTopRatedMovies,
            getUpcomingMovies
        )

        // Then - state should be loading
        assertTrue(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.popular.isEmpty())
    }

    @Test
    fun `error with cached data shows data and no error field`() = runTest {
        // Given - error but with cached data
        val cachedMovies = TestData.sampleMovies
        val errorMessage = "Network error"
        
        coEvery { getPopularMovies() } returns flowOf(
            Resource.Loading(data = cachedMovies),
            Resource.Error(errorMessage, data = cachedMovies)
        )
        coEvery { getNowPlayingMovies() } returns flowOf(Resource.Loading())
        coEvery { getTopRatedMovies() } returns flowOf(Resource.Loading())
        coEvery { getUpcomingMovies() } returns flowOf(Resource.Loading())

        // When
        viewModel = HomeViewModel(
            getPopularMovies,
            getNowPlayingMovies,
            getTopRatedMovies,
            getUpcomingMovies
        )

        // Then - should show cached data, not error field
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(cachedMovies, state.popular)
            // Error field should be null because we have data
            assertEquals(null, state.error)
        }
    }

    @Test
    fun `refresh event triggers data reload`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        coEvery { getPopularMovies() } returns flowOf(Resource.Success(movies))
        coEvery { getNowPlayingMovies() } returns flowOf(Resource.Success(movies))
        coEvery { getTopRatedMovies() } returns flowOf(Resource.Success(movies))
        coEvery { getUpcomingMovies() } returns flowOf(Resource.Success(movies))

        viewModel = HomeViewModel(
            getPopularMovies,
            getNowPlayingMovies,
            getTopRatedMovies,
            getUpcomingMovies
        )

        // When - refresh event
        viewModel.onEvent(HomeContract.Event.Refresh)

        // Then - verify use cases were called again (2 times total: init + refresh)
        io.mockk.coVerify(exactly = 2) { getPopularMovies() }
    }

    @Test
    fun `movie clicked emits navigate effect`() = runTest {
        // Given
        coEvery { getPopularMovies() } returns flowOf(Resource.Success(emptyList()))
        coEvery { getNowPlayingMovies() } returns flowOf(Resource.Success(emptyList()))
        coEvery { getTopRatedMovies() } returns flowOf(Resource.Success(emptyList()))
        coEvery { getUpcomingMovies() } returns flowOf(Resource.Success(emptyList()))

        viewModel = HomeViewModel(
            getPopularMovies,
            getNowPlayingMovies,
            getTopRatedMovies,
            getUpcomingMovies
        )

        // When - movie clicked
        val movieId = 123
        viewModel.effect.test {
            viewModel.onEvent(HomeContract.Event.MovieClicked(movieId))

            // Then - should emit NavigateToDetail effect
            val effect = awaitItem()
            assertEquals(HomeContract.Effect.NavigateToDetail(movieId), effect)
        }
    }

    @Test
    fun `all four categories load independently`() = runTest {
        // Given - different data for each category
        val popularMovies = listOf(TestData.createMovie(id = 1, title = "Popular"))
        val nowPlayingMovies = listOf(TestData.createMovie(id = 2, title = "Now Playing"))
        val topRatedMovies = listOf(TestData.createMovie(id = 3, title = "Top Rated"))
        val upcomingMovies = listOf(TestData.createMovie(id = 4, title = "Upcoming"))

        coEvery { getPopularMovies() } returns flowOf(Resource.Success(popularMovies))
        coEvery { getNowPlayingMovies() } returns flowOf(Resource.Success(nowPlayingMovies))
        coEvery { getTopRatedMovies() } returns flowOf(Resource.Success(topRatedMovies))
        coEvery { getUpcomingMovies() } returns flowOf(Resource.Success(upcomingMovies))

        // When
        viewModel = HomeViewModel(
            getPopularMovies,
            getNowPlayingMovies,
            getTopRatedMovies,
            getUpcomingMovies
        )

        // Then - each category should have correct data
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(popularMovies, state.popular)
            assertEquals(nowPlayingMovies, state.nowPlaying)
            assertEquals(topRatedMovies, state.topRated)
            assertEquals(upcomingMovies, state.upcoming)
        }
    }
}