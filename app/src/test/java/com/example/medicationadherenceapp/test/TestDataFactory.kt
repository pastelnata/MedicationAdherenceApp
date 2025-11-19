package com.example.medicationadherenceapp.test

import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.local.entities.User
import java.time.LocalDateTime
import java.util.UUID

/**
 * Test data factory for creating mock entities and DTOs for testing.
 * Provides convenient methods to generate test data with sensible defaults.
 */
object TestDataFactory {

    fun createTestUser(
        userId: UUID = UUID.randomUUID(),
        name: String = "Test User",
        password: String = "testPassword123",
        userType: UserType = UserType.PATIENT
    ) = User(
        userId = userId,
        name = name,
        password = password,
        userType = userType
    )

    fun createTestMedication(
        medicationId: UUID = UUID.randomUUID(),
        patientId: UUID = UUID.randomUUID(),
        name: String = "Test Medication",
        dosage: String = "10mg",
        instructions: String = "Take with food"
    ) = Medication(
        medicationId = medicationId,
        patientId = patientId,
        name = name,
        dosage = dosage,
        instructions = instructions
    )

    fun createTestMedicationSchedule(
        scheduleId: UUID = UUID.randomUUID(),
        medicationId: UUID = UUID.randomUUID(),
        patientId: UUID = UUID.randomUUID(),
        scheduledTime: String = "08:00",
        frequency: String = "Daily"
    ) = MedicationSchedule(
        scheduleId = scheduleId,
        medicationId = medicationId,
        patientId = patientId,
        scheduledTime = scheduledTime,
        frequency = frequency
    )

    fun createTestIntakeRecord(
        intakeId: UUID = UUID.randomUUID(),
        scheduleId: UUID = UUID.randomUUID(),
        patientId: UUID = UUID.randomUUID(),
        taken: Boolean = true,
        takenAt: String = LocalDateTime.now().toString(),
        notes: String? = null
    ) = MedicationIntakeRecord(
        intakeId = intakeId,
        scheduleId = scheduleId,
        patientId = patientId,
        taken = taken,
        takenAt = takenAt,
        notes = notes
    )

    /**
     * Creates a list of test medications for testing list operations.
     */
    fun createTestMedicationList(
        count: Int = 3,
        patientId: UUID = UUID.randomUUID()
    ): List<Medication> {
        return (1..count).map { index ->
            createTestMedication(
                patientId = patientId,
                name = "Medication $index",
                dosage = "${index * 10}mg"
            )
        }
    }

    /**
     * Creates a list of test schedules for testing.
     */
    fun createTestScheduleList(
        count: Int = 3,
        medicationId: UUID = UUID.randomUUID(),
        patientId: UUID = UUID.randomUUID()
    ): List<MedicationSchedule> {
        val times = listOf("08:00", "12:00", "18:00", "22:00")
        return (0 until count).map { index ->
            createTestMedicationSchedule(
                medicationId = medicationId,
                patientId = patientId,
                scheduledTime = times[index % times.size],
                frequency = "Daily"
            )
        }
    }
}

