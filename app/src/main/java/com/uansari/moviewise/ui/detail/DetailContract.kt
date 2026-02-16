package com.uansari.moviewise.ui.detail

import com.uansari.moviewise.domain.model.MovieDetail

object DetailContract {

    data class State(
        val isLoading: Boolean = false,
        val movieDetail: MovieDetail? = null,
        val isInWatchlist: Boolean = false,
        val error: String? = null
    )

    sealed interface Event {
        data object AddToWatchlist : Event
        data object RemoveFromWatchlist : Event

        data object RetryRequest : Event
        data object NavigateBack : Event
    }

    sealed interface Effect {
        data object GoBack : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
