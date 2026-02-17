package com.uansari.moviewise.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.ui.components.EmptyScreen
import com.uansari.moviewise.ui.components.ErrorScreen
import com.uansari.moviewise.ui.components.LoadingScreen
import com.uansari.moviewise.ui.components.MovieCard
import com.uansari.moviewise.ui.components.MovieSectionShimmer

@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit, viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // One-time Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigateToDetail -> {
                    onNavigateToDetail(effect.movieId)
                }

                is HomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    HomeContent(
        state = state, snackbarHostState = snackbarHostState, onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeContract.State,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (HomeContract.Event) -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "MovieWise", fontWeight = FontWeight.Bold
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->

        Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            when {

                // First launch, no cache, loading
                state.isLoading && !state.hasData -> {
                    LoadingScreen()
                }

                // API failed, no cache to show
                state.error != null && !state.hasData -> {
                    ErrorScreen(
                        message = state.error, onRetry = { onEvent(HomeContract.Event.Refresh) })
                }

                // Loaded successfully but empty (rare)
                state.isEmpty -> {
                    EmptyScreen(
                        title = "No movies available", subtitle = "Pull down to refresh"
                    )
                }

                // Has data — show lists with pull-to-refresh
                else -> {
                    /**
                     * PullToRefreshBox wraps the scrollable content.
                     *
                     * isRefreshing drives the visual indicator at the top.
                     * onRefresh sends a Refresh Event to the ViewModel.
                     *
                     * The movie lists stay fully visible while isRefreshing = true.
                     * The indicator appears above them, not replacing them.
                     *
                     * This is the key UX difference from isLoading:
                     * isLoading  → replace content with spinner/shimmer
                     * isRefreshing → overlay indicator on top of content
                     */
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onEvent(HomeContract.Event.Refresh) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MovieListContent(
                            state = state, onMovieClick = { movieId ->
                                onEvent(HomeContract.Event.MovieClicked(movieId))
                            })
                    }
                }
            }
        }
    }
}


@Composable
private fun MovieListContent(
    state: HomeContract.State, onMovieClick: (Int) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            MovieSection(
                title = "Upcoming",
                movies = state.upcoming,
                isLoading = state.isLoading && state.upcoming.isEmpty(),
                onMovieClick = onMovieClick
            )
        }
        item {
            MovieSection(
                title = "Now Playing",
                movies = state.nowPlaying,
                isLoading = state.isLoading && state.nowPlaying.isEmpty(),
                onMovieClick = onMovieClick
            )
        }

        item {
            MovieSection(
                title = "Popular",
                movies = state.popular,
                isLoading = state.isLoading && state.popular.isEmpty(),
                onMovieClick = onMovieClick
            )
        }

        item {
            MovieSection(
                title = "Top Rated",
                movies = state.topRated,
                isLoading = state.isLoading && state.topRated.isEmpty(),
                onMovieClick = onMovieClick
            )
        }

    }
}

@Composable
private fun MovieSection(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    if (movies.isEmpty() && !isLoading) return

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp
            )
        )

        if (isLoading) {
            // Show shimmer placeholders while this section loads
            MovieSectionShimmer()
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie, onClick = onMovieClick
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}