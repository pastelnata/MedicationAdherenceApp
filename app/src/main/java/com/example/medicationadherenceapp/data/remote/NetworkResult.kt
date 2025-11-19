package com.example.medicationadherenceapp.data.remote

/**
 * Sealed class to represent network operation results.
 * Provides type-safe handling of success, error, and loading states.
 *
 * Usage in repositories:
 * ```
 * when (result) {
 *     is NetworkResult.Success -> // handle data
 *     is NetworkResult.Error -> // handle error
 *     is NetworkResult.Loading -> // show loading state
 * }
 * ```
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function to map Success data to another type.
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> this
        is NetworkResult.Loading -> this
    }
}

/**
 * Extension function to get data or null.
 */
fun <T> NetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResult.Success -> data
        else -> null
    }
}

