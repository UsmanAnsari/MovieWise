package com.uansari.moviewise.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wrapper returned by all TMDB list endpoints.
 */
@Serializable
data class MovieListResponseDto(

    @SerialName("page")
    val page: Int,

    @SerialName("results")
    val results: List<MovieDto>,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("total_results")
    val totalResults: Int
)