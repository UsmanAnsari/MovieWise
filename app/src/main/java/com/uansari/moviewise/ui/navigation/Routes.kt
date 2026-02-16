package com.uansari.moviewise.ui.navigation

/**
 * Route definitions for the entire app.
 */
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val DETAIL = "detail/{movieId}"
    const val WATCHLIST = "watchlist"

    // Helper to build the Detail route with a real movieId
    fun detail(movieId: Int) = "detail/$movieId"
}