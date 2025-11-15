package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.data.local.entities.HealthTip
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data Transfer Objects for HealthTip-related API operations.
 */

data class HealthTipDto(
    @SerializedName("tip_id") val tipId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class CreateHealthTipRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)

// Extension functions for conversion
fun HealthTipDto.toEntity(): HealthTip = HealthTip(
    tipId = UUID.fromString(tipId),
    title = title,
    description = description,
    timestamp = timestamp
)

fun HealthTip.toDto(): HealthTipDto = HealthTipDto(
    tipId = tipId.toString(),
    title = title,
    description = description,
    timestamp = timestamp
)
