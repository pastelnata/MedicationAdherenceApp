package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.data.local.entities.User
import java.util.UUID

/**
 * Data Transfer Objects for User-related API calls.
 * These DTOs represent the JSON structure sent/received from the server.
 */

data class LoginRequest(
    val name: String,
    val password: String
)

data class LoginResponse(
    val userId: String,
    val name: String,
    val userType: String,
    val token: String? = null
)

data class RegisterRequest(
    val name: String,
    val password: String,
    val userType: String
)

data class RegisterResponse(
    val userId: String,
    val name: String,
    val userType: String,
    val message: String
)

data class UserDto(
    val userId: String,
    val name: String,
    val userType: String
)

// Extension function to convert DTO to Entity
fun UserDto.toEntity(): User {
    return User(
        userId = UUID.fromString(userId),
        name = name,
        password = "", // Password not returned from server for security
        userType = UserType.valueOf(userType.uppercase())
    )
}

// Extension function to convert Entity to DTO
fun User.toDto(): UserDto {
    return UserDto(
        userId = userId.toString(),
        name = name,
        userType = userType.name
    )
}

