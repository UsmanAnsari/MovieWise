package com.uansari.moviewise.ui.search

import androidx.paging.PagingData
import app.cash.turbine.test
import com.uansari.moviewise.domain.usecase.SearchMoviesUseCase
import com.uansari.moviewise.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var searchMovies: SearchMoviesUseCase
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        searchMovies = mockk()
    }

    @Test
    fun `initial state has empty query`() {
        // When
        viewModel = SearchViewModel(searchMovies)

        // Then
        assertEquals("", viewModel.state.value.query)
    }

    @Test
    fun `same query twice is filtered by distinctUntilChanged`() = runTest {
        // Given
        coEvery { searchMovies("test") } returns flowOf(PagingData.from(emptyList()))

        viewModel = SearchViewModel(searchMovies)

        // When - search same query twice
        viewModel.onEvent(SearchContract.Event.QueryChanged("test"))
        advanceTimeBy(300)

        viewModel.onEvent(SearchContract.Event.QueryChanged("test"))
        advanceTimeBy(300)

        // Then - search should only be called once (distinctUntilChanged)
        io.mockk.coVerify(exactly = 1) { searchMovies("test") }
    }

    @Test
    fun `movie clicked emits navigate effect`() = runTest {
        // Given
        viewModel = SearchViewModel(searchMovies)

        // When
        val movieId = 123
        viewModel.effect.test {
            viewModel.onEvent(SearchContract.Event.MovieClicked(movieId))

            // Then
            val effect = awaitItem()
            assertEquals(SearchContract.Effect.NavigateToDetail(movieId), effect)
        }
    }
}