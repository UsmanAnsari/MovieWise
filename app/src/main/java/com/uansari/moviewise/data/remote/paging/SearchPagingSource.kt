package com.uansari.moviewise.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.uansari.moviewise.data.mappers.toDomain
import com.uansari.moviewise.data.remote.api.TmdbApiService
import com.uansari.moviewise.domain.model.Movie
import okio.IOException
import retrofit2.HttpException

class SearchPagingSource(
    private val apiService: TmdbApiService, private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {

        // Initial load has no key — default to page 1
        val page = params.key ?: 1

        return try {
            // Fetch from TMDB API
            val response = apiService.searchMovies(
                query = query, page = page
            )

            // Map DTOs → Domain Models
            val movies = response.results.map { it.toDomain() }

            // Return success with pagination keys
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )

        } catch (exception: IOException) {
            // Network failure — no internet, timeout, etc.
            LoadResult.Error(exception)

        } catch (exception: HttpException) {
            // HTTP error — 404, 500, etc.
            LoadResult.Error(exception)

        } catch (exception: Exception) {
            // Any other error
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Find the page closest to the current scroll position
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}