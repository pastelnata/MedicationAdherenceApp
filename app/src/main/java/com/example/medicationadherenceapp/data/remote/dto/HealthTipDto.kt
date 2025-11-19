package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.data.local.entities.HealthTip
import java.util.UUID

/**
 * Data Transfer Objects for HealthTip-related API calls.
 */

data class HealthTipDto(
    val tipId: String,
    val title: String,
    val description: String,
    val timestamp: Long
)

data class HealthTipsResponse(
    val tips: List<HealthTipDto>
)

// Extension functions to convert between DTOs and Entities

fun HealthTipDto.toEntity(): HealthTip {
    return HealthTip(
        tipId = UUID.fromString(tipId),
        title = title,
        description = description,
        timestamp = timestamp
    )
}

fun HealthTip.toDto(): HealthTipDto {
    return HealthTipDto(
        tipId = tipId.toString(),
        title = title,
        description = description,
        timestamp = timestamp
    )
}

