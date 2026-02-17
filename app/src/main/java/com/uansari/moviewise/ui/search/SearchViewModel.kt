package com.uansari.moviewise.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.uansari.moviewise.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchContract.State())
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()

    private val _effect = Channel<SearchContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    fun onEvent(event: SearchContract.Event) {
        when (event) {
            is SearchContract.Event.QueryChanged -> {
                _state.update { it.copy(query = event.query) }
                queryFlow.value = event.query
            }

            is SearchContract.Event.MovieClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        SearchContract.Effect.NavigateToDetail(event.movieId)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            queryFlow.debounce(300)                    // Wait 300ms after user stops typing
                .filter { it.length >= 2 }        // Only search for 2+ characters
                .distinctUntilChanged()           // Skip if same as last search
                .flatMapLatest { query ->
                    // Cancel previous search, start new one
                    // flatMapLatest cancels the old Flow when a new one starts
                    searchMovies(query)
                }.cachedIn(viewModelScope)         // Keep data across config changes
                .collect { pagingData ->
                    _state.update {
                        it.copy(
                            searchResults = kotlinx.coroutines.flow.flowOf(
                                pagingData
                            )
                        )
                    }
                }
        }
    }
}