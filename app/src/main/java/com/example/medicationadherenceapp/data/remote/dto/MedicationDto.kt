package com.example.medicationadherenceapp.data.remote.dto

import com.example.medicationadherenceapp.MedStatus
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data Transfer Objects for Medication-related API operations.
 */

data class MedicationDto(
    @SerializedName("medication_id") val medicationId: String,
    @SerializedName("name") val name: String,
    @SerializedName("dosage_mg") val dosageMg: Float
)

data class MedicationScheduleDto(
    @SerializedName("schedule_id") val scheduleId: String,
    @SerializedName("patient_id") val patientId: String,
    @SerializedName("medication_id") val medicationId: String,
    @SerializedName("scheduled_time") val scheduledTime: Long,
    @SerializedName("status") val status: String
)

data class MedicationIntakeDto(
    @SerializedName("intake_id") val intakeId: String,
    @SerializedName("schedule_id") val scheduleId: String,
    @SerializedName("taken_at") val takenAt: Long
)

data class CreateMedicationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("dosage_mg") val dosageMg: Float
)

data class CreateScheduleRequest(
    @SerializedName("patient_id") val patientId: String,
    @SerializedName("medication_id") val medicationId: String,
    @SerializedName("scheduled_time") val scheduledTime: Long
)

data class RecordIntakeRequest(
    @SerializedName("schedule_id") val scheduleId: String,
    @SerializedName("taken_at") val takenAt: Long
)

data class UpdateScheduleStatusRequest(
    @SerializedName("status") val status: String
)

// Extension functions for conversion
fun MedicationDto.toEntity(): Medication = Medication(
    medicationId = UUID.fromString(medicationId),
    name = name,
    dosageMg = dosageMg
)

fun Medication.toDto(): MedicationDto = MedicationDto(
    medicationId = medicationId.toString(),
    name = name,
    dosageMg = dosageMg
)

fun MedicationScheduleDto.toEntity(): MedicationSchedule = MedicationSchedule(
    scheduleId = UUID.fromString(scheduleId),
    patientId = UUID.fromString(patientId),
    medicationId = UUID.fromString(medicationId),
    scheduledTime = scheduledTime,
    status = MedStatus.valueOf(status.uppercase())
)

fun MedicationSchedule.toDto(): MedicationScheduleDto = MedicationScheduleDto(
    scheduleId = scheduleId.toString(),
    patientId = patientId.toString(),
    medicationId = medicationId.toString(),
    scheduledTime = scheduledTime,
    status = status.name
)

fun MedicationIntakeDto.toEntity(): MedicationIntakeRecord = MedicationIntakeRecord(
    intakeId = UUID.fromString(intakeId),
    scheduleId = UUID.fromString(scheduleId),
    takenAt = takenAt
)

fun MedicationIntakeRecord.toDto(): MedicationIntakeDto = MedicationIntakeDto(
    intakeId = intakeId.toString(),
    scheduleId = scheduleId.toString(),
    takenAt = takenAt
)
