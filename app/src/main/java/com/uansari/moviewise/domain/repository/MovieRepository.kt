package com.uansari.moviewise.domain.repository

import androidx.paging.PagingData
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

/**
 * Defines WHAT data operations are available in this app.
 * Does not define HOW they are implemented.
 *
 * WHY AN INTERFACE HERE:
 * The domain layer must not depend on the data layer.
 * By defining an interface in domain/ and implementing it in data/,
 * we invert the dependency — data depends on domain, not the other way around.
 *
 * This also makes testing trivial:
 * In tests, inject a FakeMovieRepository instead of the real one.
 * No network calls. No database. Full control over what data is returned.
 *
 * RETURN TYPES:
 * Flow<Resource<T>>  → for single-shot operations with loading/error states
 * Flow<List<Movie>>  → for watchlist (always reads from Room, no loading state)
 * Flow<PagingData<Movie>> → for search (Paging 3 manages its own state)
 * Flow<Boolean>      → for watchlist membership check (live Room query)
 */
interface MovieRepository {

    // Home Screen

    fun getPopularMovies(): Flow<Resource<List<Movie>>>

    fun getNowPlayingMovies(): Flow<Resource<List<Movie>>>

    fun getTopRatedMovies(): Flow<Resource<List<Movie>>>

    fun getUpcomingMovies(): Flow<Resource<List<Movie>>>

    // Detail Screen

    fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>>

    // Search

    fun searchMovies(query: String): Flow<PagingData<Movie>>

    // Watchlist

    fun getWatchlist(): Flow<List<Movie>>

    fun isMovieInWatchlist(movieId: Int): Flow<Boolean>

    suspend fun addToWatchlist(movie: Movie)

    suspend fun removeFromWatchlist(movieId: Int)
}

