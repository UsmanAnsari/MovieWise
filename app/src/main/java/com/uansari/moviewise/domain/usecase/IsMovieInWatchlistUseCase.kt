package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Live check — returns a Flow<Boolean> that emits true/false
 * whenever the watchlist changes. The Detail screen button
 * updates automatically with no polling or manual refresh.
 */
class IsMovieInWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> =
        repository.isMovieInWatchlist(movieId)
}