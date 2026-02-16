package com.uansari.moviewise.data.remote.api

import com.uansari.moviewise.data.remote.dto.MovieDetailDto
import com.uansari.moviewise.data.remote.dto.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines all TMDB API endpoints.
 */
interface TmdbApiService {

    // Home Screen Endpoints

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1
    ): MovieListResponseDto


    // Detail Screen Endpoint

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): MovieDetailDto


    // Search Endpoint (used by Paging 3)

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String, @Query("page") page: Int = 1
    ): MovieListResponseDto
}