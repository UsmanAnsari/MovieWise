package com.uansari.moviewise.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.uansari.moviewise.data.local.dao.MovieDao
import com.uansari.moviewise.data.local.dao.WatchlistDao
import com.uansari.moviewise.data.mappers.toDomain
import com.uansari.moviewise.data.mappers.toWatchlistEntity
import com.uansari.moviewise.data.remote.api.TmdbApiService
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
    override fun getPopularMovies(): Flow<Resource<List<Movie>>> = fetchMovieList {
        apiService.getPopularMovies().results.map { it.toDomain(MovieCategory.POPULAR) }
    }

    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> = fetchMovieList {
        apiService.getNowPlayingMovies().results.map { it.toDomain(MovieCategory.NOW_PLAYING) }
    }

    override fun getTopRatedMovies(): Flow<Resource<List<Movie>>> = fetchMovieList {
        apiService.getTopRatedMovies().results.map { it.toDomain(MovieCategory.TOP_RATED) }
    }

    override fun getUpcomingMovies(): Flow<Resource<List<Movie>>> = fetchMovieList {
        apiService.getUpcomingMovies().results.map { it.toDomain(MovieCategory.UPCOMING) }
    }

    /**
     * Private helper that wraps every home screen API call with
     * the same Loading → Success/Error emission pattern.
     */
    private fun fetchMovieList(
        fetch: suspend () -> List<Movie>
    ): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val movies = fetch()
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = e.message ?: "Failed to load movies"
                )
            )
        }
    }

    // Detail Screen

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
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                SearchPagingSourcePlaceholder(apiService, query)
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