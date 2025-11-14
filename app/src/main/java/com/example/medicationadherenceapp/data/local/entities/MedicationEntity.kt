package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicationadherenceapp.MedStatus
import java.util.UUID

// Medication represents a single medication meta-record (name, dosage, id).
// Use this table to store medication definitions that may be referenced by
// schedules. The primary key `medicationId` is a UUID so items remain stable
// across sync operations.
@Entity
data class Medication(
    @PrimaryKey val medicationId: UUID = UUID.randomUUID(),
    val name: String,
    val dosageMg: Float
)


// MedicationSchedule represents a scheduled occurrence of a medication for a
// particular patient. It references the `User` (patientId) and the
// `Medication` (medicationId). The `scheduledTime` is stored as epoch millis
// so it's simple to compare against System.currentTimeMillis in workers and
// UI filters. The `status` field is an enum that tracks DUE/TAKEN/OVERDUE, etc.
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Medication::class,
            parentColumns = ["medicationId"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"]), Index(value = ["medicationId"]) ]
)
data class MedicationSchedule(
    @PrimaryKey val scheduleId: UUID = UUID.randomUUID(),
    val patientId: UUID,
    val medicationId: UUID,
    // store scheduled time as epoch millis
    val scheduledTime: Long,
    val status: MedStatus = MedStatus.DUE //default status
)

// MedicationIntakeRecord stores when a scheduled dose was taken (or missed if
// you record missed instances). It references `MedicationSchedule` and uses a
// timestamp so the app can show historical adherence and compute metrics.
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MedicationSchedule::class,
            parentColumns = ["scheduleId"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["scheduleId"]) ]
)
data class MedicationIntakeRecord(
    @PrimaryKey val intakeId: UUID = UUID.randomUUID(),
    val scheduleId: UUID,
    // store takenAt as epoch millis
    val takenAt: Long
)
