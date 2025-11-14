package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

// EmergencyContact stores contact details for a patient. The foreign key
// constraint keeps contacts tied to an existing patient, and the index on
// `patientId` optimizes queries that fetch contacts for a single patient.
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)
data class EmergencyContact(
    @PrimaryKey val contactId: UUID = UUID.randomUUID(),
    val patientId: UUID,
    val name: String,
    val phone: String,
    val priority: String
)
