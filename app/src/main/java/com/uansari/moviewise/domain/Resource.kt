package com.uansari.moviewise.domain

/**
 * A generic wrapper that represents the state of a data operation.
 *
 * USAGE EXAMPLE in a ViewModel:
 *
 *   when (result) {
 *       is Resource.Loading -> showLoader()
 *       is Resource.Success -> showMovies(result.data)
 *       is Resource.Error   -> showError(result.message)
 *   }
 *
 * T is the type of data on success (e.g. List<Movie>, MovieDetail)
 */
sealed class Resource<out T> {

    /**
     * Operation is in progress.
     * data is optional — allows showing stale cache while loading fresh data.
     */
    data class Loading<out T>(val data: T? = null) : Resource<T>()

    /**
     * Operation completed successfully.
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Operation failed.
     * data is optional — allows showing stale cache alongside the error message.
     */
    data class Error<out T>(
        val message: String,
        val data: T? = null
    ) : Resource<T>()
}