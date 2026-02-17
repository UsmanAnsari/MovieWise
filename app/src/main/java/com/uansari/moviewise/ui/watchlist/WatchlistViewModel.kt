package com.uansari.moviewise.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.moviewise.domain.usecase.GetWatchlistUseCase
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
class WatchlistViewModel @Inject constructor(
    private val getWatchlist: GetWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistContract.State())
    val state: StateFlow<WatchlistContract.State> = _state.asStateFlow()

    private val _effect = Channel<WatchlistContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeWatchlist()
    }

    fun onEvent(event: WatchlistContract.Event) {
        when (event) {
            is WatchlistContract.Event.RemoveMovie -> removeMovie(
                movieId = event.movieId,
                title = event.title
            )
            is WatchlistContract.Event.MovieClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        WatchlistContract.Effect.NavigateToDetail(event.movieId)
                    )
                }
            }
        }
    }

    private fun observeWatchlist() {
        viewModelScope.launch {
            getWatchlist().collect { movies ->
                _state.update {
                    it.copy(
                        movies = movies,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun removeMovie(movieId: Int, title: String) {
        viewModelScope.launch {
            removeFromWatchlist(movieId)
            _effect.send(
                WatchlistContract.Effect.ShowMessage("\"$title\" removed from watchlist")
            )
        }
    }
}