package com.example.medicationadherenceapp.data.remote.dto

/**
 * Generic API response wrapper for handling success/error states.
 */

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
)

data class ErrorResponse(
    val message: String,
    val errorCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

