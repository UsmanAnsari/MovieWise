package com.uansari.moviewise.domain.usecase

import androidx.paging.PagingData
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves the movies list using search.
 *
 * Paging 3 manages its own loading/error states internally via LoadState,
 * so this returns Flow<PagingData<Movie>> rather than Flow<Resource<...>>.
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Movie>> =
        repository.searchMovies(query)
}