package com.example.medicationadherenceapp.data.remote

import com.example.medicationadherenceapp.data.remote.dto.Result
import retrofit2.Response
import java.io.IOException

/**
 * Extension function to safely call API and wrap the result in Result sealed class.
 * Handles exceptions and HTTP errors consistently.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(
                    exception = IOException("Response body is null"),
                    message = "Empty response from server"
                )
            }
        } else {
            Result.Error(
                exception = IOException("HTTP ${response.code()}: ${response.message()}"),
                message = response.message()
            )
        }
    } catch (e: Exception) {
        Result.Error(
            exception = e,
            message = e.message ?: "Unknown error occurred"
        )
    }
}

/**
 * Extension to handle API call with automatic retry logic.
 */
suspend fun <T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    apiCall: suspend () -> Response<T>
): Result<T> {
    var lastException: Exception? = null
    
    repeat(maxRetries) { attempt ->
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body)
                }
            } else if (response.code() in 400..499) {
                // Don't retry client errors
                return Result.Error(
                    exception = IOException("HTTP ${response.code()}: ${response.message()}"),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries - 1) {
                // Wait before retry (exponential backoff)
                kotlinx.coroutines.delay((1000L * (attempt + 1)))
            }
        }
    }
    
    return Result.Error(
        exception = lastException ?: IOException("Max retries exceeded"),
        message = "Failed after $maxRetries attempts"
    )
}
