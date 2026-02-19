package com.uansari.moviewise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.usecase.GetNowPlayingMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetPopularMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetTopRatedMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val getNowPlayingMovies: GetNowPlayingMoviesUseCase,
    private val getTopRatedMovies: GetTopRatedMoviesUseCase,
    private val getUpcomingMovies: GetUpcomingMoviesUseCase
) : ViewModel() {

    // State

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    // Effects

    /**
     * Channel ensures each Effect is delivered exactly once.
     */
    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Tracks the coroutine running all 4 category loads.
     * Cancelling moviesJob stops all 4 child coroutines simultaneously —
     * essential so a refresh doesn't run alongside the previous load.
     */
    private var moviesJob: Job? = null

    // Init
    init {
        loadMovies()
    }

    // Event Handler

    fun onEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.Refresh -> loadMovies()
            is HomeContract.Event.MovieClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        HomeContract.Effect.NavigateToDetail(event.movieId)
                    )
                }
            }
        }
    }

    // Data Loading

    private fun loadMovies() {
        moviesJob?.cancel()

        // Clear error so a stale error message doesn't show
        // alongside newly loading content
        _state.update { it.copy(error = null) }

        moviesJob = viewModelScope.launch {
            // supervisorScope ensures one failing category doesn't
            // cancel the other three — each loads independently
            supervisorScope {
                launch { collectUpcoming() }
                launch { collectNowPlaying() }
                launch { collectPopular() }
                launch { collectTopRated() }
            }
        }
    }


    private suspend fun collectPopular() {
        getPopularMovies().collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    val hasCachedData = resource.data?.isNotEmpty() == true
                    _state.update { state ->
                        state.copy(
                            isLoading = !hasCachedData, isRefreshing = hasCachedData,
                            // Show cached data immediately if we have it
                            popular = resource.data?.takeIf { it.isNotEmpty() } ?: state.popular)
                    }
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false, isRefreshing = false, popular = resource.data
                    )
                }

                is Resource.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false, isRefreshing = false,
                            // Keep showing cached data if available
                            popular = resource.data?.takeIf { it.isNotEmpty() } ?: state.popular,
                            // Only set error field if we have no data to show
                            error = if (resource.data.isNullOrEmpty()) resource.message
                            else state.error)
                    }
                    // If we had cached data, show Snackbar instead of error screen
                    if (!resource.data.isNullOrEmpty()) {
                        _effect.send(
                            HomeContract.Effect.ShowError("Showing cached data — ${resource.message}")
                        )
                    }
                }
            }
        }
    }

    private suspend fun collectNowPlaying() {
        getNowPlayingMovies().collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    val hasCachedData = resource.data?.isNotEmpty() == true
                    _state.update { state ->
                        state.copy(
                            isLoading = if (hasCachedData) state.isLoading else true,
                            isRefreshing = if (hasCachedData) true else state.isRefreshing,
                            nowPlaying = resource.data?.takeIf { it.isNotEmpty() }
                                ?: state.nowPlaying)
                    }
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false, isRefreshing = false, nowPlaying = resource.data
                    )
                }

                is Resource.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            nowPlaying = resource.data?.takeIf { it.isNotEmpty() }
                                ?: state.nowPlaying)
                    }
                }
            }
        }
    }

    private suspend fun collectTopRated() {
        getTopRatedMovies().collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    val hasCachedData = resource.data?.isNotEmpty() == true
                    _state.update { state ->
                        state.copy(
                            isLoading = if (hasCachedData) state.isLoading else true,
                            isRefreshing = if (hasCachedData) true else state.isRefreshing,
                            topRated = resource.data?.takeIf { it.isNotEmpty() } ?: state.topRated)
                    }
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false, isRefreshing = false, topRated = resource.data
                    )
                }

                is Resource.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            topRated = resource.data?.takeIf { it.isNotEmpty() } ?: state.topRated)
                    }
                }
            }
        }
    }

    private suspend fun collectUpcoming() {
        getUpcomingMovies().collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    val hasCachedData = resource.data?.isNotEmpty() == true
                    _state.update { state ->
                        state.copy(
                            isLoading = if (hasCachedData) state.isLoading else true,
                            isRefreshing = if (hasCachedData) true else state.isRefreshing,
                            upcoming = resource.data?.takeIf { it.isNotEmpty() } ?: state.upcoming)
                    }
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false, isRefreshing = false, upcoming = resource.data
                    )
                }

                is Resource.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            upcoming = resource.data?.takeIf { it.isNotEmpty() } ?: state.upcoming)
                    }
                }
            }
        }
    }
}