package com.example.medicationadherenceapp.data.remote.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * Interceptor for logging HTTP requests and responses.
 * Useful for debugging API calls during development.
 */
class LoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "NetworkLog"
        private const val MAX_BODY_LOG_LENGTH = 1000
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Log request
        Log.d(TAG, "╔════════════════════════════════════════════════════════════")
        Log.d(TAG, "║ REQUEST: ${request.method} ${request.url}")
        Log.d(TAG, "║ Headers: ${request.headers}")
        request.body?.let { body ->
            Log.d(TAG, "║ Content-Type: ${body.contentType()}")
            Log.d(TAG, "║ Content-Length: ${body.contentLength()}")
        }
        
        val response: Response
        try {
            val startTime = System.currentTimeMillis()
            response = chain.proceed(request)
            val duration = System.currentTimeMillis() - startTime
            
            // Log response
            Log.d(TAG, "╠════════════════════════════════════════════════════════════")
            Log.d(TAG, "║ RESPONSE: ${response.code} ${response.message}")
            Log.d(TAG, "║ URL: ${response.request.url}")
            Log.d(TAG, "║ Duration: ${duration}ms")
            Log.d(TAG, "║ Headers: ${response.headers}")
            
            // Log response body (read once and create new response)
            val responseBody = response.body
            if (responseBody != null) {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer entire body
                val buffer = source.buffer
                val bodyString = buffer.clone().readString(Charsets.UTF_8)
                
                val logBody = if (bodyString.length > MAX_BODY_LOG_LENGTH) {
                    bodyString.substring(0, MAX_BODY_LOG_LENGTH) + "... (truncated)"
                } else {
                    bodyString
                }
                
                Log.d(TAG, "║ Body: $logBody")
                
                // Create new response with buffered body
                val newResponseBody = bodyString.toResponseBody(responseBody.contentType())
                Log.d(TAG, "╚════════════════════════════════════════════════════════════")
                
                return response.newBuilder()
                    .body(newResponseBody)
                    .build()
            }
            
            Log.d(TAG, "╚════════════════════════════════════════════════════════════")
            
        } catch (e: IOException) {
            Log.e(TAG, "╠════════════════════════════════════════════════════════════")
            Log.e(TAG, "║ ERROR: Network request failed", e)
            Log.e(TAG, "╚════════════════════════════════════════════════════════════")
            throw e
        }
        
        return response
    }
}
