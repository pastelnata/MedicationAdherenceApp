package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmergencyContact(
    @PrimaryKey val contactId: UUID = UUID.randomUUID(),
    val patientId: UUID,
    val name: String,
    val phone: String,
    val priority: String
)
