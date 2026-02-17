package com.uansari.moviewise.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.uansari.moviewise.data.local.dao.MovieDao
import com.uansari.moviewise.data.local.dao.WatchlistDao
import com.uansari.moviewise.data.mappers.toDomain
import com.uansari.moviewise.data.mappers.toEntity
import com.uansari.moviewise.data.mappers.toWatchlistEntity
import com.uansari.moviewise.data.remote.api.TmdbApiService
import com.uansari.moviewise.data.remote.paging.SearchPagingSource
import com.uansari.moviewise.data.util.networkBoundResource
import com.uansari.moviewise.domain.Resource
import com.uansari.moviewise.domain.model.Movie
import com.uansari.moviewise.domain.model.MovieDetail
import com.uansari.moviewise.domain.repository.MovieRepository
import com.uansari.moviewise.domain.util.MovieCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of MovieRepository.
 *
 * Receives all dependencies via Hilt constructor injection.
 * @Singleton ensures one instance exists for the app's lifetime.
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    private val movieDao: MovieDao,
    private val watchlistDao: WatchlistDao
) : MovieRepository {

    // Home Screen
    override fun getPopularMovies(): Flow<Resource<List<Movie>>> = networkBoundResource(query = {
        movieDao.getMoviesByCategory(MovieCategory.POPULAR)
            .map { entities -> entities.map { it.toDomain() } }
    }, fetch = {
        apiService.getPopularMovies()
    }, saveFetchResult = { response ->
        // Clear stale cache for this category, then insert fresh data.
        // Done as two separate operations (not a transaction) because
        // the brief gap is acceptable — the Room Flow will emit the
        // empty state for a split second before re-emitting with fresh data.
        movieDao.deleteMoviesByCategory(MovieCategory.POPULAR)
        movieDao.insertMovies(
            response.results.map { it.toEntity(MovieCategory.POPULAR) })
    })

    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> = networkBoundResource(query = {
        movieDao.getMoviesByCategory(MovieCategory.NOW_PLAYING)
            .map { entities -> entities.map { it.toDomain() } }
    }, fetch = {
        apiService.getNowPlayingMovies()
    }, saveFetchResult = { response ->
        movieDao.deleteMoviesByCategory(MovieCategory.NOW_PLAYING)
        movieDao.insertMovies(
            response.results.map { it.toEntity(MovieCategory.NOW_PLAYING) })
    })

    override fun getTopRatedMovies(): Flow<Resource<List<Movie>>> = networkBoundResource(query = {
        movieDao.getMoviesByCategory(MovieCategory.TOP_RATED)
            .map { entities -> entities.map { it.toDomain() } }
    }, fetch = {
        apiService.getTopRatedMovies()
    }, saveFetchResult = { response ->
        movieDao.deleteMoviesByCategory(MovieCategory.TOP_RATED)
        movieDao.insertMovies(
            response.results.map { it.toEntity(MovieCategory.TOP_RATED) })
    })

    override fun getUpcomingMovies(): Flow<Resource<List<Movie>>> = networkBoundResource(query = {
        movieDao.getMoviesByCategory(MovieCategory.UPCOMING)
            .map { entities -> entities.map { it.toDomain() } }
    }, fetch = {
        apiService.getUpcomingMovies()
    }, saveFetchResult = { response ->
        movieDao.deleteMoviesByCategory(MovieCategory.UPCOMING)
        movieDao.insertMovies(
            response.results.map { it.toEntity(MovieCategory.UPCOMING) })
    })

    // Detail Screen - API only

    override fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        emit(Resource.Loading())
        try {
            val detail = apiService.getMovieDetail(movieId).toDomain()
            emit(Resource.Success(detail))
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Failed to load movie details"
                )
            )
        }
    }

    // Search (Paging 3)

    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,              // TMDB returns 20 per page by default
                enablePlaceholders = false, initialLoadSize = 20
            ), pagingSourceFactory = {
                SearchPagingSource(apiService, query)
            }).flow
    }

    // Watchlist

    override fun getWatchlist(): Flow<List<Movie>> = watchlistDao.getWatchlist().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun isMovieInWatchlist(movieId: Int): Flow<Boolean> =
        watchlistDao.isInWatchlist(movieId)

    override suspend fun addToWatchlist(movie: Movie) {
        watchlistDao.addToWatchlist(movie.toWatchlistEntity())
    }

    override suspend fun removeFromWatchlist(movieId: Int) {
        watchlistDao.removeFromWatchlist(movieId)
    }
}