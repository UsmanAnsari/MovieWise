package com.uansari.moviewise.util

import com.uansari.moviewise.BuildConfig

object Constants {

    const val BASE_URL = BuildConfig.TMDB_BASE_URL
    const val IMAGE_BASE_URL = BuildConfig.TMDB_IMAGE_BASE_URL
    const val API_KEY = BuildConfig.TMDB_API_KEY

    // Image size paths used with IMAGE_BASE_URL
    const val IMAGE_SIZE_POSTER = "w500"      // Movie posters
    const val IMAGE_SIZE_BACKDROP = "w1280"   // Full backdrop images
    const val IMAGE_SIZE_PROFILE = "w185"     // Cast profile photos

    // Full image URL helper — used in Coil across the app
    fun posterUrl(path: String?) = "$IMAGE_BASE_URL$IMAGE_SIZE_POSTER$path"
    fun backdropUrl(path: String?) = "$IMAGE_BASE_URL$IMAGE_SIZE_BACKDROP$path"
    fun profileUrl(path: String?) = "$IMAGE_BASE_URL$IMAGE_SIZE_PROFILE$path"
}