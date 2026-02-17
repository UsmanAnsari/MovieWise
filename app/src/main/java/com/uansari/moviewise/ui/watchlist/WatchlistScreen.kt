package com.uansari.moviewise.ui.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.util.formatToRatingString
import com.uansari.moviewise.ui.components.EmptyScreen
import com.uansari.moviewise.ui.theme.RatingStarColor

@Composable
fun WatchlistScreen(
    onNavigateToDetail: (Int) -> Unit, viewModel: WatchlistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WatchlistContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.movieId)

                is WatchlistContract.Effect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    WatchlistContent(
        state = state, snackbarHostState = snackbarHostState, onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistContent(
    state: WatchlistContract.State,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (WatchlistContract.Event) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Watchlist", fontWeight = FontWeight.Bold
                    )
                    // Movie count subtitle — only shown when list is non-empty
                    if (state.movies.isNotEmpty()) {
                        Text(
                            text = "${state.movies.size} " + if (state.movies.size == 1) "movie" else "movies",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {

                // Initial Room load — brief spinner
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // No saved movies
                state.isEmpty -> {
                    EmptyScreen(
                        title = "Your watchlist is empty",
                        subtitle = "Save movies from the detail screen\nto watch them later"
                    )
                }

                // Watchlist with movies
                else -> {
                    WatchlistMovieList(movies = state.movies, onMovieClick = { movieId ->
                        onEvent(WatchlistContract.Event.MovieClicked(movieId))
                    }, onRemove = { movieId, title ->
                        onEvent(
                            WatchlistContract.Event.RemoveMovie(
                                movieId = movieId, title = title
                            )
                        )
                    })
                }
            }
        }
    }
}

@Composable
private fun WatchlistMovieList(
    movies: List<Movie>, onMovieClick: (Int) -> Unit, onRemove: (Int, String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = movies, key = { it.id }) { movie ->
            WatchlistMovieItem(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
                onRemove = { onRemove(movie.id, movie.title) })
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun WatchlistMovieItem(
    movie: Movie, onClick: () -> Unit, onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {


        AsyncImage(
            model = movie.posterPath,
            contentDescription = "${movie.title} poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 60.dp, height = 88.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Rating + Year row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = RatingStarColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = movie.voteAverage.formatToRatingString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )

                // Release year (safe substring)
                if (movie.releaseDate.length >= 4) {
                    Text(
                        text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = movie.releaseDate.take(4),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Overview excerpt
            if (movie.overview.isNotBlank()) {
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove ${movie.title} from watchlist",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}