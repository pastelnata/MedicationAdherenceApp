package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// HealthTip is a simple content entity designed to show tips in the app.
// Storing a timestamp allows ordering and simple caching strategies.
@Entity
data class HealthTip(
    @PrimaryKey val tipId: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val timestamp: Long
)
