package com.example.medicationadherenceapp.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Interceptor for handling network errors consistently.
 * Wraps low-level exceptions in more meaningful error messages.
 */
class ErrorHandlingInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        try {
            val response = chain.proceed(request)
            
            // Check for HTTP error codes
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                
                // You can parse the error body here and throw custom exceptions
                when (response.code) {
                    401 -> throw UnauthorizedException("Authentication failed")
                    403 -> throw ForbiddenException("Access forbidden")
                    404 -> throw NotFoundException("Resource not found")
                    500 -> throw ServerException("Internal server error")
                    in 500..599 -> throw ServerException("Server error: ${response.code}")
                }
            }
            
            return response
            
        } catch (e: UnknownHostException) {
            throw NetworkException("No internet connection available", e)
        } catch (e: SocketTimeoutException) {
            throw NetworkException("Connection timeout", e)
        } catch (e: IOException) {
            throw NetworkException("Network error occurred", e)
        }
    }
}

// Custom exceptions for different error types
class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)
class UnauthorizedException(message: String) : IOException(message)
class ForbiddenException(message: String) : IOException(message)
class NotFoundException(message: String) : IOException(message)
class ServerException(message: String) : IOException(message)
