package com.uansari.moviewise.ui.home

import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.ui.components.MovieCard

@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit, viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // One-time Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigateToDetail -> {
                    onNavigateToDetail(effect.movieId)
                }

                is HomeContract.Effect.ShowError -> {
                    Log.e("HOMESCREEN", effect.message)
                }
            }
        }
    }

    HomeContent(
        state = state, onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeContract.State, onEvent: (HomeContract.Event) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MovieWise", fontWeight = FontWeight.Bold
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when {

                // Loading State
                state.isLoading && state.popular.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error State
                state.error != null && state.popular.isEmpty() -> {
                    ErrorContent(
                        message = state.error,
                        onRetry = { onEvent(HomeContract.Event.Refresh) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Success State
                else -> {
                    MovieListContent(
                        state = state, onMovieClick = { movieId ->
                            onEvent(HomeContract.Event.MovieClicked(movieId))
                        })
                }
            }
        }
    }
}

@Composable
private fun MovieListContent(
    state: HomeContract.State, onMovieClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (state.nowPlaying.isNotEmpty()) {
            item {
                MovieSection(
                    title = "Now Playing", movies = state.nowPlaying, onMovieClick = onMovieClick
                )
            }
        }

        if (state.popular.isNotEmpty()) {
            item {
                MovieSection(
                    title = "Popular", movies = state.popular, onMovieClick = onMovieClick
                )
            }
        }

        if (state.topRated.isNotEmpty()) {
            item {
                MovieSection(
                    title = "Top Rated", movies = state.topRated, onMovieClick = onMovieClick
                )
            }
        }

        if (state.upcoming.isNotEmpty()) {
            item {
                MovieSection(
                    title = "Upcoming", movies = state.upcoming, onMovieClick = onMovieClick
                )
            }
        }
    }
}

@Composable
private fun MovieSection(
    title: String, movies: List<Movie>, onMovieClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = movies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie, onClick = onMovieClick
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun ErrorContent(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}
