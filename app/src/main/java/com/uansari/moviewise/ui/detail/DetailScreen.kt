package com.uansari.moviewise.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.uansari.moviewise.domain.model.CastMember
import com.uansari.moviewise.domain.model.MovieDetail
import com.uansari.moviewise.domain.util.formatToRatingString
import com.uansari.moviewise.ui.components.ErrorScreen
import com.uansari.moviewise.ui.components.LoadingScreen
import com.uansari.moviewise.ui.theme.RatingStarColor

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit, viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailContract.Effect.GoBack -> onNavigateBack()
                is DetailContract.Effect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    DetailContent(
        state = state, snackbarHostState = snackbarHostState, onEvent = viewModel::onEvent
    )
}

@Composable
fun DetailContent(
    state: DetailContract.State,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (DetailContract.Event) -> Unit
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
        // Only show FAB when movie is loaded
        state.movieDetail?.let {
            WatchlistFab(
                isInWatchlist = state.isInWatchlist,
                onAdd = { onEvent(DetailContract.Event.AddToWatchlist) },
                onRemove = { onEvent(DetailContract.Event.RemoveFromWatchlist) })
        }
    }) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                state.isLoading -> LoadingScreen()

                state.error != null && state.movieDetail == null -> {
                    ErrorScreen(
                        message = state.error, onRetry = {
                            onEvent(DetailContract.Event.RetryRequest)
                        })
                }

                state.movieDetail != null -> {
                    MovieDetailContent(
                        movie = state.movieDetail, onNavigateBack = {
                            onEvent(DetailContract.Event.NavigateBack)
                        })
                }
            }
        }
    }
}

// Main Content

@Composable
private fun MovieDetailContent(
    movie: MovieDetail, onNavigateBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp) // Space for FAB
    ) {
        // Hero Header
        item {
            MovieHeader(
                movie = movie, onNavigateBack = onNavigateBack
            )
        }

        // Overview
        item {
            if (movie.overview.isNotBlank()) {
                SectionTitle(title = "Overview")
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Cast
        item {
            if (movie.cast.isNotEmpty()) {
                SectionTitle(title = "Cast")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = movie.cast, key = { it.id }) { castMember ->
                        CastMemberCard(castMember = castMember)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Movie Header

@Composable
private fun MovieHeader(
    movie: MovieDetail, onNavigateBack: () -> Unit
) {
    Box {
        // Backdrop Image
        AsyncImage(
            model = movie.backdropPath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        // Gradient overlay — fades backdrop into background colour
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Back button
        IconButton(
            onClick = onNavigateBack, modifier = Modifier
                .padding(8.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f), shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Poster + Info row — overlaps the bottom of the backdrop
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Poster
            AsyncImage(
                model = movie.posterPath,
                contentDescription = "${movie.title} poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(95.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .offset(y = 40.dp)  // Extends below backdrop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title + metadata
            Column(
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating + Runtime row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = RatingStarColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = movie.voteAverage.formatToRatingString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    if (movie.runtime > 0) {
                        Text(
                            text = "•", color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = formatRuntime(movie.runtime),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Release year
                if (movie.releaseDate.length >= 4) {
                    Text(
                        text = movie.releaseDate.take(4),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }

    // Space for the poster offset + genre chips
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
    ) {
        // Tagline
        if (movie.tagline.isNotBlank()) {
            Text(
                text = "\"${movie.tagline}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Genre chips
        if (movie.genres.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movie.genres) { genre ->
                    SuggestionChip(onClick = { }, label = {
                        Text(
                            text = genre, style = MaterialTheme.typography.labelSmall
                        )
                    })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Cast Member Card

@Composable
private fun CastMemberCard(castMember: CastMember) {
    Column(
        modifier = Modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = castMember.profilePath,
            contentDescription = castMember.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = castMember.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = castMember.character,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Watchlist FAB

@Composable
private fun WatchlistFab(
    isInWatchlist: Boolean, onAdd: () -> Unit, onRemove: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = if (isInWatchlist) onRemove else onAdd, icon = {
        Icon(
            imageVector = if (isInWatchlist) Icons.Default.Bookmark
            else Icons.Default.BookmarkBorder, contentDescription = null
        )
    }, text = {
        Text(
            text = if (isInWatchlist) "In Watchlist" else "Add to Watchlist"
        )
    }, containerColor = if (isInWatchlist) MaterialTheme.colorScheme.secondary
    else MaterialTheme.colorScheme.primary
    )
}

// Helpers

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp
        )
    )
}

/**
 * Converts runtime in minutes to a readable string.
 * 148 → "2h 28m"
 * 45  → "45m"
 */
private fun formatRuntime(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}m"
    }
}