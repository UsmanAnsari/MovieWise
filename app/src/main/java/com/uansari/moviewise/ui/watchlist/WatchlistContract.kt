package com.uansari.moviewise.ui.watchlist

import com.uansari.moviewise.domain.model.Movie

object WatchlistContract {

    data class State(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true  // True on init until first Room emission
    ) {
        val isEmpty: Boolean
            get() = !isLoading && movies.isEmpty()
    }

    sealed interface Event {
        data class RemoveMovie(val movieId: Int, val title: String) : Event
        data class MovieClicked(val movieId: Int) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val movieId: Int) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}