package com.uansari.moviewise.ui.watchlist

import app.cash.turbine.test
import com.uansari.moviewise.domain.usecase.GetWatchlistUseCase
import com.uansari.moviewise.domain.usecase.RemoveFromWatchlistUseCase
import com.uansari.moviewise.util.MainDispatcherRule
import com.uansari.moviewise.util.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WatchlistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getWatchlist: GetWatchlistUseCase
    private lateinit var removeFromWatchlist: RemoveFromWatchlistUseCase
    private lateinit var viewModel: WatchlistViewModel

    @Before
    fun setup() {
        getWatchlist = mockk()
        removeFromWatchlist = mockk(relaxed = true)
    }

    @Test
    fun `watchlist loads successfully`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        coEvery { getWatchlist() } returns flowOf(movies)

        // When
        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(movies, state.movies)
        }
    }

    @Test
    fun `empty watchlist shows isEmpty state`() = runTest {
        // Given
        coEvery { getWatchlist() } returns flowOf(emptyList())

        // When
        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
            assertEquals(0, state.movies.size)
        }
    }

    @Test
    fun `remove movie calls use case with correct id`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        coEvery { getWatchlist() } returns flowOf(movies)

        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist)

        // When
        val movieId = 123
        val movieTitle = "Test Movie"
        viewModel.onEvent(
            WatchlistContract.Event.RemoveMovie(movieId, movieTitle)
        )

        // Then
        coVerify { removeFromWatchlist(movieId) }
    }

    @Test
    fun `remove movie emits show message effect`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        coEvery { getWatchlist() } returns flowOf(movies)

        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist)

        // When
        val movieId = 123
        val movieTitle = "Inception"

        viewModel.effect.test {
            viewModel.onEvent(
                WatchlistContract.Event.RemoveMovie(movieId, movieTitle)
            )

            // Then
            val effect = awaitItem()
            assertTrue(effect is WatchlistContract.Effect.ShowMessage)
            val message = (effect as WatchlistContract.Effect.ShowMessage).message
            assertTrue(message.contains(movieTitle))
        }
    }

    @Test
    fun `movie clicked emits navigate effect`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        coEvery { getWatchlist() } returns flowOf(movies)

        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist)

        // When
        val movieId = 123
        viewModel.effect.test {
            viewModel.onEvent(WatchlistContract.Event.MovieClicked(movieId))

            // Then
            val effect = awaitItem()
            assertEquals(
                WatchlistContract.Effect.NavigateToDetail(movieId), effect
            )
        }
    }
}