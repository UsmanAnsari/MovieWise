package com.uansari.moviewise.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.ui.components.EmptyScreen
import com.uansari.moviewise.ui.components.ErrorScreen
import com.uansari.moviewise.ui.components.LoadingScreen
import com.uansari.moviewise.ui.components.MovieCard

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit, viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val searchResults = state.searchResults.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
            }
        }
    }

    SearchContent(
        query = state.query, searchResults = searchResults, onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    query: String, searchResults: LazyPagingItems<Movie>, onEvent: (SearchContract.Event) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Search", fontWeight = FontWeight.Bold)
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    onEvent(SearchContract.Event.QueryChanged(newQuery))
                },
                placeholder = { Text("Search for movies...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                singleLine = true
            )

            when (val refreshState = searchResults.loadState.refresh) {

                is LoadState.Loading -> {
                    if (query.isNotBlank()) {
                        LoadingScreen()
                    }
                }

                is LoadState.Error -> {
                    ErrorScreen(
                        message = refreshState.error.message ?: "Failed to load search results",
                        onRetry = { searchResults.retry() })
                }

                is LoadState.NotLoading -> {
                    when {
                        query.isBlank() -> {
                            EmptyScreen(
                                title = "Search for movies",
                                subtitle = "Type at least 2 characters to search"
                            )
                        }

                        query.length < 2 -> {
                            EmptyScreen(
                                title = "Keep typing...", subtitle = "Enter at least 2 characters"
                            )
                        }

                        searchResults.itemCount == 0 -> {
                            EmptyScreen(
                                icon = Icons.Outlined.SearchOff,
                                title = "No results for \"$query\"",
                                subtitle = "Try a different search term"
                            )
                        }

                        else -> {
                            SearchResultsGrid(
                                searchResults = searchResults, onMovieClick = { movieId ->
                                    onEvent(SearchContract.Event.MovieClicked(movieId))
                                })
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchResultsGrid(
    searchResults: LazyPagingItems<Movie>, onMovieClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = searchResults.itemCount,
            key = { index -> searchResults[index]?.id ?: index }) { index ->
            searchResults[index]?.let { movie ->
                MovieCard(
                    movie = movie, onClick = onMovieClick
                )
            }
        }

        /**
         * loadState.append handles loading the next page.
         * We show a spinner at the bottom while loading,
         * or a retry button if the append failed.
         */
        when (val appendState = searchResults.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    AppendErrorItem(
                        message = appendState.error.message ?: "Failed to load more",
                        onRetry = { searchResults.retry() })
                }
            }

            is LoadState.NotLoading -> {
                // End of pagination or waiting for scroll
            }
        }
    }
}


@Composable
private fun AppendErrorItem(
    message: String, onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}