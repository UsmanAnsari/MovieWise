package com.uansari.moviewise.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.uansari.moviewise.data.mappers.toDomain
import com.uansari.moviewise.data.remote.api.TmdbApiService
import com.uansari.moviewise.domain.model.Movie

/**
 * Temporary placeholder for SearchPagingSource.
 */
class SearchPagingSourcePlaceholder(
    private val apiService: TmdbApiService, private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = apiService.searchMovies(query, page)
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                anchor
            )?.nextKey?.minus(1)
        }
    }
}