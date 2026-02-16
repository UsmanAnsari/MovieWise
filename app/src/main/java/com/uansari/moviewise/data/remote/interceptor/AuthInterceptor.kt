package com.uansari.moviewise.data.remote.interceptor

import com.uansari.moviewise.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Automatically attaches the TMDB API key to every outgoing request.
 *
 * WHY AN INTERCEPTOR:
 * Instead of manually adding the API key to every single function call
 * in TmdbApiService, we intercept the request once here and add it globally.
 * This means if the auth mechanism ever changes,
 * we change it in ONE place only.
 *
 * TMDB accepts the key as a query parameter: ?api_key=xxx
 */
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Take the original URL and add api_key as a query parameter
        val urlWithApiKey = originalRequest.url
            .newBuilder()
            .addQueryParameter("api_key", Constants.API_KEY)
            .build()

        // Build a new request with the updated URL
        val authenticatedRequest = originalRequest
            .newBuilder()
            .url(urlWithApiKey)
            .build()

        return chain.proceed(authenticatedRequest)
    }
}