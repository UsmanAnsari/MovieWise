package com.uansari.moviewise.data.util

import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Converts exceptions into user-friendly error messages.
 *
 * WHY THIS EXISTS:
 * Raw exceptions like "SocketTimeoutException" or "HTTP 500"
 * mean nothing to users. This translates them into actionable messages.
 *
 * USAGE:
 * catch (e: Exception) {
 *     val message = e.toUserFriendlyMessage()
 *     emit(Resource.Error(message))
 * }
 */
fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {

        // Network Connectivity
        is UnknownHostException -> "No internet connection. Check your network and try again."

        is SocketTimeoutException -> "Request timed out. Check your connection and try again."

        is IOException -> "Network error. Please check your connection."

        // HTTP Errors
        is HttpException -> when (code()) {
            401 -> "Authentication failed. Please check your API key."
            404 -> "The requested content was not found."
            429 -> "Too many requests. Please wait a moment and try again."
            500, 502, 503 -> "Server error. Please try again later."
            else -> "Server returned error ${code()}. Please try again."
        }

        // Fallback
        else -> message ?: "An unexpected error occurred. Please try again."
    }
}
