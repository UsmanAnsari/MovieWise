package com.uansari.moviewise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.usecase.GetNowPlayingMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetPopularMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetTopRatedMoviesUseCase
import com.uansari.moviewise.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        loadPopular()
        loadNowPlaying()
        loadTopRated()
        loadUpcoming()
    }

    /**
     * Each category loads independently in its own coroutine.
     *
     * WHY SEPARATE COROUTINES:
     * If all 4 calls were sequential, a slow "upcoming" response
     * would block "popular" from showing. With separate launches,
     * each list appears as soon as its own response arrives —
     * the screen progressively fills in.
     */
    private fun loadPopular() {
        viewModelScope.launch {
            getPopularMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update {
                        it.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, popular = resource.data)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    private fun loadNowPlaying() {
        viewModelScope.launch {
            getNowPlayingMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, nowPlaying = resource.data)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    private fun loadTopRated() {
        viewModelScope.launch {
            getTopRatedMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, topRated = resource.data)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    private fun loadUpcoming() {
        viewModelScope.launch {
            getUpcomingMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, upcoming = resource.data)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }
}