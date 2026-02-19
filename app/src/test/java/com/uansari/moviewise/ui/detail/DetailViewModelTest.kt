package com.uansari.moviewise.ui.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.toMovie
import com.uansari.moviewise.domain.usecase.AddToWatchlistUseCase
import com.uansari.moviewise.domain.usecase.GetMovieDetailUseCase
import com.uansari.moviewise.domain.usecase.IsMovieInWatchlistUseCase
import com.uansari.moviewise.domain.usecase.RemoveFromWatchlistUseCase
import com.uansari.moviewise.util.MainDispatcherRule
import com.uansari.moviewise.util.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getMovieDetail: GetMovieDetailUseCase
    private lateinit var addToWatchlist: AddToWatchlistUseCase
    private lateinit var removeFromWatchlist: RemoveFromWatchlistUseCase
    private lateinit var isMovieInWatchlist: IsMovieInWatchlistUseCase

    private lateinit var viewModel: DetailViewModel

    private val testMovieId = 123

    @Before
    fun setup() {
        savedStateHandle = mockk(relaxed = true)
        every { savedStateHandle.get<Int>("movieId") } returns testMovieId

        getMovieDetail = mockk()
        addToWatchlist = mockk(relaxed = true)
        removeFromWatchlist = mockk(relaxed = true)
        isMovieInWatchlist = mockk()
    }

    @Test
    fun `initial state is loading`() {
        // Given
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Loading())
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        // When
        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // Then
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `detail loads successfully`() = runTest {
        // Given
        val movieDetail = TestData.createMovieDetail(id = testMovieId)
        coEvery { getMovieDetail(testMovieId) } returns flowOf(
            Resource.Loading(),
            Resource.Success(movieDetail)
        )
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        // When
        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.movieDetail)
            assertEquals(movieDetail, state.movieDetail)
        }
    }

    @Test
    fun `error loading detail shows error message`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getMovieDetail(testMovieId) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        // When
        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `watchlist status updates correctly`() = runTest {
        // Given
        val movieDetail = TestData.createMovieDetail(id = testMovieId)
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(true)

        // When
        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isInWatchlist)
        }
    }

    @Test
    fun `add to watchlist calls use case`() = runTest {
        // Given
        val movieDetail = TestData.createMovieDetail(id = testMovieId, title = "Test Movie")
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // When
        viewModel.onEvent(DetailContract.Event.AddToWatchlist)

        // Then
        coVerify { addToWatchlist(movieDetail.toMovie()) }
    }

    @Test
    fun `remove from watchlist calls use case`() = runTest {
        // Given
        val movieDetail = TestData.createMovieDetail(id = testMovieId)
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(true)

        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // When
        viewModel.onEvent(DetailContract.Event.RemoveFromWatchlist)

        // Then
        coVerify { removeFromWatchlist(testMovieId) }
    }

    @Test
    fun `add to watchlist emits success message effect`() = runTest {
        // Given
        val movieTitle = "Inception"
        val movieDetail = TestData.createMovieDetail(id = testMovieId, title = movieTitle)
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // When
        viewModel.effect.test {
            viewModel.onEvent(DetailContract.Event.AddToWatchlist)

            // Then
            val effect = awaitItem()
            assertTrue(effect is DetailContract.Effect.ShowMessage)
            val message = (effect as DetailContract.Effect.ShowMessage).message
            assertTrue(message.contains(movieTitle))
        }
    }

    @Test
    fun `navigate back emits go back effect`() = runTest {
        // Given
        val movieDetail = TestData.createMovieDetail(id = testMovieId)
        coEvery { getMovieDetail(testMovieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { isMovieInWatchlist(testMovieId) } returns flowOf(false)

        viewModel = DetailViewModel(
            savedStateHandle,
            getMovieDetail,
            addToWatchlist,
            removeFromWatchlist,
            isMovieInWatchlist
        )

        // When
        viewModel.effect.test {
            viewModel.onEvent(DetailContract.Event.NavigateBack)

            // Then
            val effect = awaitItem()
            assertEquals(DetailContract.Effect.GoBack, effect)
        }
    }
}