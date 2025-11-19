package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.MedStatus
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import java.util.UUID

/**
 * Data Transfer Objects for Medication-related API calls.
 */

data class MedicationDto(
    val medicationId: String,
    val name: String,
    val dosageMg: Float
)

data class MedicationScheduleDto(
    val scheduleId: String,
    val patientId: String,
    val medicationId: String,
    val scheduledTime: Long,
    val status: String
)

data class MedicationIntakeRecordDto(
    val intakeId: String,
    val scheduleId: String,
    val takenAt: Long
)

data class CreateMedicationRequest(
    val name: String,
    val dosageMg: Float
)

data class CreateScheduleRequest(
    val patientId: String,
    val medicationId: String,
    val scheduledTime: Long
)

data class RecordIntakeRequest(
    val scheduleId: String,
    val takenAt: Long
)

// Extension functions to convert between DTOs and Entities

fun MedicationDto.toEntity(): Medication {
    return Medication(
        medicationId = UUID.fromString(medicationId),
        name = name,
        dosageMg = dosageMg
    )
}

fun Medication.toDto(): MedicationDto {
    return MedicationDto(
        medicationId = medicationId.toString(),
        name = name,
        dosageMg = dosageMg
    )
}

fun MedicationScheduleDto.toEntity(): MedicationSchedule {
    return MedicationSchedule(
        scheduleId = UUID.fromString(scheduleId),
        patientId = UUID.fromString(patientId),
        medicationId = UUID.fromString(medicationId),
        scheduledTime = scheduledTime,
        status = MedStatus.valueOf(status.uppercase())
    )
}

fun MedicationSchedule.toDto(): MedicationScheduleDto {
    return MedicationScheduleDto(
        scheduleId = scheduleId.toString(),
        patientId = patientId.toString(),
        medicationId = medicationId.toString(),
        scheduledTime = scheduledTime,
        status = status.name
    )
}

fun MedicationIntakeRecordDto.toEntity(): MedicationIntakeRecord {
    return MedicationIntakeRecord(
        intakeId = UUID.fromString(intakeId),
        scheduleId = UUID.fromString(scheduleId),
        takenAt = takenAt
    )
}

fun MedicationIntakeRecord.toDto(): MedicationIntakeRecordDto {
    return MedicationIntakeRecordDto(
        intakeId = intakeId.toString(),
        scheduleId = scheduleId.toString(),
        takenAt = takenAt
    )
}

