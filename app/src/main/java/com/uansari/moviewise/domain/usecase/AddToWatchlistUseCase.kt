package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Add the movie to the watch list.
 */
class AddToWatchlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) = repository.addToWatchlist(movie)
}
