package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves the watch list movies.
 */
class GetWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repository.getWatchlist()
}
