package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.User
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data Transfer Objects for User-related API operations.
 * These DTOs separate network representation from local entities.
 */

data class LoginRequest(
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("user_type") val userType: String,
    @SerializedName("token") val token: String? = null
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_type") val userType: String
)

data class UserDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_type") val userType: String
)

data class FamilyMemberDto(
    @SerializedName("family_member_id") val familyMemberId: String,
    @SerializedName("name") val name: String,
    @SerializedName("patient_ids") val patientIds: List<String>? = null
)

data class LinkFamilyRequest(
    @SerializedName("family_member_id") val familyMemberId: String,
    @SerializedName("patient_id") val patientId: String
)

// Extension functions to convert between DTOs and entities
fun UserDto.toEntity(): User = User(
    userId = UUID.fromString(userId),
    name = name,
    password = password,
    userType = UserType.valueOf(userType.uppercase())
)

fun User.toDto(): UserDto = UserDto(
    userId = userId.toString(),
    name = name,
    password = password,
    userType = userType.name
)

fun FamilyMemberDto.toEntity(): FamilyMember = FamilyMember(
    familyMemberId = UUID.fromString(familyMemberId),
    name = name
)

fun FamilyMember.toDto(): FamilyMemberDto = FamilyMemberDto(
    familyMemberId = familyMemberId.toString(),
    name = name
)
