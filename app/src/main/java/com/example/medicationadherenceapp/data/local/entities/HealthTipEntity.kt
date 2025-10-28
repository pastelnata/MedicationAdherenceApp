package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class HealthTip(
    @PrimaryKey val tipId: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val timestamp: Long
)
