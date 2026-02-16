package com.uansari.moviewise.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.toMovie
import com.uansari.moviewise.domain.usecase.AddToWatchlistUseCase
import com.uansari.moviewise.domain.usecase.GetMovieDetailUseCase
import com.uansari.moviewise.domain.usecase.IsMovieInWatchlistUseCase
import com.uansari.moviewise.domain.usecase.RemoveFromWatchlistUseCase
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
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val addToWatchlist: AddToWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase,
    private val isMovieInWatchlist: IsMovieInWatchlistUseCase
) : ViewModel() {

    // Extract movieId from the navigation back stack
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _state = MutableStateFlow(DetailContract.State())
    val state: StateFlow<DetailContract.State> = _state.asStateFlow()

    private val _effect = Channel<DetailContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        request()
    }

    fun onEvent(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.AddToWatchlist -> addCurrentMovieToWatchlist()
            is DetailContract.Event.RemoveFromWatchlist -> removeCurrentMovieFromWatchlist()
            is DetailContract.Event.NavigateBack -> {
                viewModelScope.launch {
                    _effect.send(DetailContract.Effect.GoBack)
                }
            }

            DetailContract.Event.RetryRequest -> {
                request()
            }
        }
    }

    private fun request() {
        loadMovieDetail()
        observeWatchlistStatus()
    }

    // Load Detail

    private fun loadMovieDetail() {
        viewModelScope.launch {
            getMovieDetail(movieId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update {
                        it.copy(isLoading = true, error = null)
                    }

                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, movieDetail = resource.data)
                    }

                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    // Watchlist Status

    /**
     * Observes Room's live watchlist query.
     * The button state updates automatically when the user adds
     * or removes from ANY screen — no manual sync needed.
     */
    private fun observeWatchlistStatus() {
        viewModelScope.launch {
            isMovieInWatchlist(movieId).collect { inWatchlist ->
                _state.update { it.copy(isInWatchlist = inWatchlist) }
            }
        }
    }

    // Watchlist Toggle

    private fun addCurrentMovieToWatchlist() {
        val movie = _state.value.movieDetail ?: return
        viewModelScope.launch {
            // Convert MovieDetail to Movie domain model for the watchlist
            val movieForWatchlist = movie.toMovie()
            addToWatchlist(movieForWatchlist)
            _effect.send(
                DetailContract.Effect.ShowMessage("${movie.title} added to watchlist")
            )
        }
    }

    private fun removeCurrentMovieFromWatchlist() {
        val movie = _state.value.movieDetail ?: return
        viewModelScope.launch {
            removeFromWatchlist(movie.id)
            _effect.send(
                DetailContract.Effect.ShowMessage("${movie.title} removed from watchlist")
            )
        }
    }
}