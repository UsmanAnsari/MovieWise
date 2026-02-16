package com.uansari.moviewise.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uansari.moviewise.ui.theme.RatingStarColor

/**
 * Star rating badge — reused in MovieCard and DetailScreen.
 */
@Composable
fun RatingBadge(
    rating: Double, modifier: Modifier = Modifier
) {
    val formattedRating = String.format("%.1f", rating)

    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = RatingStarColor,
            modifier = Modifier
                .height(12.dp)
                .width(12.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = formattedRating,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}