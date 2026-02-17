package com.uansari.moviewise.ui.search

import androidx.paging.PagingData
import com.uansari.moviewise.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

object SearchContract {

    data class State(
        val query: String = "", val searchResults: Flow<PagingData<Movie>> = emptyFlow()
    )

    sealed interface Event {
        data class QueryChanged(val query: String) : Event
        data class MovieClicked(val movieId: Int) : Event
    }

    sealed interface Effect {
        data class NavigateToDetail(val movieId: Int) : Effect
    }
}