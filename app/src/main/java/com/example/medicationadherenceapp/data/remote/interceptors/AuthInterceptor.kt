package com.example.medicationadherenceapp.data.remote.interceptors

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicationadherenceapp.data.datastore.DataStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor to add authentication token to all API requests.
 * Retrieves token from DataStore and adds it to the Authorization header.
 */
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager
) : Interceptor {
    
    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_TYPE = "Bearer"
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding token for login/register endpoints
        val skipAuth = originalRequest.url.encodedPath.contains("/auth/")
        
        if (skipAuth) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from DataStore (blocking for interceptor context)
        val token = runBlocking {
            try {
                dataStoreManager.getPreference(AUTH_TOKEN_KEY, "").first()
            } catch (e: Exception) {
                ""
            }
        }
        
        // Add token to request if available
        val newRequest = if (token.isNotBlank()) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, "$TOKEN_TYPE $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
