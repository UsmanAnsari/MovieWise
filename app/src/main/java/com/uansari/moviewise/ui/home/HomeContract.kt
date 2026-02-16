package com.uansari.moviewise.ui.home

import com.uansari.moviewise.domain.model.Movie

/**
 * MVI Contract for the Home screen.
 *
 *
 * State  → The complete picture of what the screen shows right now.
 *           One State object drives the entire UI. No separate flags scattered
 *           across the ViewModel.
 *
 * Event  → Actions the user can take on this screen.
 *           Screen sends Events to ViewModel. ViewModel never calls
 *           UI functions directly.
 *
 * Effect → One-time side effects that don't belong in State.
 *           Navigation is the classic example — once you navigate,
 *           you don't want to navigate again if the screen recomposes.
 */
object HomeContract {

    data class State(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val nowPlaying: List<Movie> = emptyList(),
        val popular: List<Movie> = emptyList(),
        val topRated: List<Movie> = emptyList(),
        val upcoming: List<Movie> = emptyList(),
        val error: String? = null
    ) {
        /**
         * True only when ALL 4 lists are empty AND we're not loading.
         * Drives the empty state UI.
         */
        val isEmpty: Boolean
            get() = !isLoading &&
                    !isRefreshing &&
                    error == null &&
                    nowPlaying.isEmpty() &&
                    popular.isEmpty() &&
                    topRated.isEmpty() &&
                    upcoming.isEmpty()

        val hasData: Boolean
            get() = nowPlaying.isNotEmpty() ||
                    popular.isNotEmpty() ||
                    topRated.isNotEmpty() ||
                    upcoming.isNotEmpty()
    }

    sealed interface Event {
        data object Refresh : Event
        data class MovieClicked(val movieId: Int) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val movieId: Int) : Effect
        data class ShowError(val message: String) : Effect
    }
}