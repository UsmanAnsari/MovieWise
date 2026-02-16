package com.uansari.moviewise.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A full shimmer row — shown in place of a MovieSection
 * while that section's data is still loading.
 */
@Composable
fun MovieSectionShimmer(modifier: Modifier = Modifier) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
        userScrollEnabled = false
    ) {
        items(5) {
            MovieCardShimmer()
        }
    }
}