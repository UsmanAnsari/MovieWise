package com.uansari.moviewise.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.uansari.moviewise.domain.model.Movie

/**
 * Reusable movie card for Home, Search, and Watchlist screens.
 *
 * Uses SubcomposeAsyncImage instead of the simpler AsyncImage because
 * SubcomposeAsyncImage lets us provide custom Loading and Error
 *
 * AsyncImage: simpler API, less UI control
 * SubcomposeAsyncImage: slightly more verbose, full composable control
 *                       over loading + error states
 */@Composable
fun MovieCard(
    movie: Movie, onClick: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(130.dp)
            .clickable { onClick(movie.id) }) {

        SubcomposeAsyncImage(
            model = movie.posterPath,
            contentDescription = "${movie.title} poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(130.dp)
                .height(195.dp)              // Standard 2:3 movie poster ratio
                .clip(RoundedCornerShape(8.dp)),
            loading = {

                // Shimmer placeholder while loading image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(shimmerBrush())
                )
            },
            error = {
                // Fallback when image URL is null or load fails
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Image unavailable",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            success = {
                // Default rendering — SubcomposeAsyncImage handles this
                // We need to explicitly call the painter here
                Image(
                    painter = painter,
                    contentDescription = "${movie.title} poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        RatingBadge(rating = movie.voteAverage)
    }
}

