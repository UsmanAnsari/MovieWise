package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Remove the movie from the watch list.
 */
class RemoveFromWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int) =
        repository.removeFromWatchlist(movieId)
}
