package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves the Top Rated movies list.
 */
class GetTopRatedMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<Resource<List<Movie>>> =
        repository.getTopRatedMovies()
}
