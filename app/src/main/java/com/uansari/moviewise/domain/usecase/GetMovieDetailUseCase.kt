package com.uansari.moviewise.domain.usecase

import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.MovieDetail
import com.uansari.moviewise.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves movie detail.
 */
class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetail>> =
        repository.getMovieDetail(movieId)
}
