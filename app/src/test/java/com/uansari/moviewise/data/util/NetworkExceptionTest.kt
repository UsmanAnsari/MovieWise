package com.uansari.moviewise.data.util

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkExceptionTest {

    @Test
    fun `UnknownHostException returns no internet message`() {
        // Given
        val exception = UnknownHostException("Unable to resolve host")

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("internet"))
        assertTrue(message.contains("network"))
    }

    @Test
    fun `SocketTimeoutException returns timeout message`() {
        // Given
        val exception = SocketTimeoutException("Timeout")

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("timed out"))
    }

    @Test
    fun `IOException returns network error message`() {
        // Given
        val exception = IOException("Network failure")

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("Network error"))
    }

    @Test
    fun `HttpException 401 returns auth failed message`() {
        // Given
        val response = Response.error<Any>(401,
            okhttp3.ResponseBody.create(null, "Unauthorized"))
        val exception = HttpException(response)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("Authentication failed"))
        assertTrue(message.contains("API key"))
    }

    @Test
    fun `HttpException 404 returns not found message`() {
        // Given
        val response = Response.error<Any>(404, 
            okhttp3.ResponseBody.create(null, "Not Found"))
        val exception = HttpException(response)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("not found"))
    }

    @Test
    fun `HttpException 429 returns rate limit message`() {
        // Given
        val response = Response.error<Any>(429, 
            okhttp3.ResponseBody.create(null, "Too Many Requests"))
        val exception = HttpException(response)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("Too many requests"))
    }

    @Test
    fun `HttpException 500 returns server error message`() {
        // Given
        val response = Response.error<Any>(500, 
            okhttp3.ResponseBody.create(null, "Internal Server Error"))
        val exception = HttpException(response)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("Server error"))
    }

    @Test
    fun `HttpException with unknown code returns generic message`() {
        // Given
        val response = Response.error<Any>(418, 
            okhttp3.ResponseBody.create(null, "I'm a teapot"))
        val exception = HttpException(response)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("418"))
        assertTrue(message.contains("Server returned error"))
    }

    @Test
    fun `generic Exception returns fallback message`() {
        // Given
        val exception = RuntimeException("Something went wrong")

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertEquals("Something went wrong", message)
    }

    @Test
    fun `Exception with null message returns default message`() {
        // Given
        val exception = RuntimeException(null as String?)

        // When
        val message = exception.toUserFriendlyMessage()

        // Then
        assertTrue(message.contains("unexpected error"))
    }
}
