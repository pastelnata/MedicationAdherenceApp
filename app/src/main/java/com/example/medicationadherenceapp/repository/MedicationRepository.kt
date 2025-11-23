package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.remote.NetworkResult
import com.example.medicationadherenceapp.data.remote.RemoteDataSource
import com.example.medicationadherenceapp.data.remote.dto.toDto
import com.example.medicationadherenceapp.data.remote.dto.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for medication domain. This class mediates access to the local
 * DAO layer and remote API, implementing an offline-first strategy.
 *
 * Offline-first strategy:
 *  - Read methods return Room Flow<T> so UI layers can observe data immediately.
 *  - Write methods write to local DB first, then sync to remote in background.
 *  - Refresh methods fetch from remote and update local cache.
 *
 * The repository provides:
 *  1. Local-first reads: Always read from Room DB (fast, works offline)
 *  2. Optimistic writes: Write to local DB immediately, sync to remote async
 *  3. Explicit refresh: Pull latest data from server on demand
 */
@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val intakeDao: MedicationIntakeDao,
    private val remoteDataSource: RemoteDataSource
) {
    // ==================== Local-first operations ====================

    suspend fun addMedication(med: Medication) {
        // Write to local DB immediately
        medicationDao.insertMedication(med)

        // TODO: Queue for background sync to remote
        // Could use WorkManager to sync when online
    }

    suspend fun scheduleMedication(schedule: MedicationSchedule) {
        // Write to local DB immediately
        medicationDao.insertMedicationSchedule(schedule)

        // TODO: Queue for background sync to remote
    }

    fun getSchedules(patientId: UUID): Flow<List<MedicationSchedule>> =
        medicationDao.getMedicationSchedules(patientId)

    suspend fun addIntake(record: MedicationIntakeRecord) {
        // Write to local DB immediately
        intakeDao.insertIntakeRecord(record)

        // TODO: Queue for background sync to remote
    }

    // ==================== CRUD operations ====================

    // Medication CRUD
    suspend fun getMedication(id: UUID) = medicationDao.getMedication(id)

    suspend fun updateMedication(med: Medication) {
        medicationDao.updateMedication(med)
        // TODO: Queue for sync
    }

    suspend fun deleteMedication(id: UUID) {
        medicationDao.deleteMedication(id)
        // TODO: Queue for sync
    }

    // Schedule CRUD
    suspend fun getSchedule(id: UUID) = medicationDao.getSchedule(id)

    suspend fun updateSchedule(schedule: MedicationSchedule) {
        medicationDao.updateMedicationSchedule(schedule)
        // TODO: Queue for sync
    }

    suspend fun deleteSchedule(id: UUID) {
        medicationDao.deleteMedicationSchedule(id)
        // TODO: Queue for sync
    }

    // Intake CRUD
    suspend fun getIntakeRecords(id: UUID) = intakeDao.getIntake(id)

    suspend fun updateIntake(record: MedicationIntakeRecord) {
        intakeDao.updateIntakeRecord(record)
        // TODO: Queue for sync
    }

    suspend fun deleteIntake(id: UUID) {
        intakeDao.deleteIntake(id)
        // TODO: Queue for sync
    }

    // ==================== Network-based refresh operations ====================

    /**
     * Refresh medications from remote server.
     * Fetches latest medications and updates local cache.
     */
    suspend fun refreshMedications(): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getMedications()) {
            is NetworkResult.Success -> {
                // Update local cache with remote data
                result.data.forEach { dto ->
                    medicationDao.insertMedication(dto.toEntity())
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Refresh schedules for a patient from remote server.
     */
    suspend fun refreshSchedules(patientId: UUID): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getSchedules(patientId.toString())) {
            is NetworkResult.Success -> {
                // Update local cache with remote data
                result.data.forEach { dto ->
                    medicationDao.insertMedicationSchedule(dto.toEntity())
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Sync local medication to remote server.
     */
    suspend fun syncMedicationToRemote(medication: Medication): NetworkResult<Unit> {
        val dto = medication.toDto()
        return when (val result = remoteDataSource.createMedication(dto.name, dto.dosageMg)) {
            is NetworkResult.Success -> {
                // Update local with server response (in case server modified it)
                medicationDao.insertMedication(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Sync local schedule to remote server.
     */
    suspend fun syncScheduleToRemote(schedule: MedicationSchedule): NetworkResult<Unit> {
        return when (val result = remoteDataSource.createSchedule(
            patientId = schedule.patientId.toString(),
            medicationId = schedule.medicationId.toString(),
            scheduledTime = schedule.scheduledTime
        )) {
            is NetworkResult.Success -> {
                // Update local with server response
                medicationDao.insertMedicationSchedule(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Sync local intake record to remote server.
     */
    suspend fun syncIntakeToRemote(intake: MedicationIntakeRecord): NetworkResult<Unit> {
        return when (val result = remoteDataSource.recordIntake(
            scheduleId = intake.scheduleId.toString(),
            takenAt = intake.takenAt
        )) {
            is NetworkResult.Success -> {
                // Update local with server response
                intakeDao.insertIntakeRecord(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}

