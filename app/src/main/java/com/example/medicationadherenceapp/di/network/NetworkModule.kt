@file:Suppress("unused")
package com.example.medicationadherenceapp.di.network

import com.example.medicationadherenceapp.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that provides network-related dependencies.
 *
 * Provides:
 * - Retrofit instance configured with base URL and converters
 * - ApiService interface implementation
 * - OkHttpClient with logging and timeout configuration
 * - Gson for JSON serialization/deserialization
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Base URL - in production, this should come from BuildConfig or a config file
    private const val BASE_URL = "https://api.medicationadherence.example.com/api/v1/"

    // For development/testing with local backend
    // private const val BASE_URL = "http://10.0.2.2:8080/api/v1/" // Android emulator
    // private const val BASE_URL = "http://localhost:8080/api/v1/" // Real device

    /**
     * Provides Gson instance with custom configuration.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .create()
    }

    /**
     * Provides OkHttpClient with logging interceptor and timeout configuration.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Set to BODY for development, NONE for production
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides ApiService implementation.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}





