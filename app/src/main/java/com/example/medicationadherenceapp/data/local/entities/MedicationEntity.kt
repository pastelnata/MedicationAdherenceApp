package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medicationadherenceapp.MedStatus
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Medication(
    @PrimaryKey val medicationId: UUID = UUID.randomUUID(),
    val name: String,
    val dosageMg: Float
)


// When the medication is scheduled
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
    ]
)
data class MedicationSchedule(
    @PrimaryKey val scheduleId: UUID = UUID.randomUUID(),
    val patientId: UUID,
    val medicationId: UUID,
    val scheduledTime: LocalDateTime,
    val status: MedStatus = MedStatus.DUE //default status
)

// When the medication was taken / missed
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MedicationSchedule::class,
            parentColumns = ["scheduleId"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MedicationIntakeRecord(
    @PrimaryKey val intakeId: UUID = UUID.randomUUID(),
    val scheduleId: UUID,
    val takenAt: LocalDateTime
)
