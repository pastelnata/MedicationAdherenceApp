package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.remote.ApiService
import com.example.medicationadherenceapp.data.remote.dto.*
import com.example.medicationadherenceapp.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for medication domain. Implements offline-first pattern with remote sync.
 * 
 * Strategy:
 * - Read operations return Flow from Room for reactive UI updates
 * - Write operations attempt remote sync, fallback to local if network fails
 * - Explicit sync methods to fetch latest data from server
 */
@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val intakeDao: MedicationIntakeDao,
    private val apiService: ApiService
) {
    
    // ==================== Medication CRUD ====================
    
    suspend fun addMedication(med: Medication): Result<Medication> {
        return try {
            val result = safeApiCall {
                apiService.createMedication(
                    CreateMedicationRequest(
                        name = med.name,
                        dosageMg = med.dosageMg
                    )
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    medicationDao.insertMedication(entity)
                    Result.Success(entity)
                }
                is Result.Error -> {
                    medicationDao.insertMedication(med)
                    Result.Success(med)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.insertMedication(med)
            Result.Success(med)
        }
    }
    
    suspend fun getMedication(id: UUID) = medicationDao.getMedication(id)
    
    suspend fun updateMedication(med: Medication): Result<Medication> {
        return try {
            val result = safeApiCall {
                apiService.updateMedication(med.medicationId.toString(), med.toDto())
            }
            
            medicationDao.updateMedication(med)
            
            when (result) {
                is Result.Success -> Result.Success(med)
                is Result.Error -> Result.Success(med) // Updated locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.updateMedication(med)
            Result.Success(med)
        }
    }
    
    suspend fun deleteMedication(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteMedication(id.toString())
            }
            
            medicationDao.deleteMedication(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.deleteMedication(id)
            Result.Success(Unit)
        }
    }
    
    /**
     * Sync all medications from server.
     */
    suspend fun syncMedications(): Result<List<Medication>> {
        return try {
            val result = safeApiCall {
                apiService.getAllMedications()
            }
            
            when (result) {
                is Result.Success -> {
                    val medications = result.data.map { it.toEntity() }
                    medications.forEach { medicationDao.insertMedication(it) }
                    Result.Success(medications)
                }
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }
    
    // ==================== Schedule Management ====================
    
    suspend fun scheduleMedication(schedule: MedicationSchedule): Result<MedicationSchedule> {
        return try {
            val result = safeApiCall {
                apiService.createSchedule(
                    CreateScheduleRequest(
                        patientId = schedule.patientId.toString(),
                        medicationId = schedule.medicationId.toString(),
                        scheduledTime = schedule.scheduledTime
                    )
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    medicationDao.insertMedicationSchedule(entity)
                    Result.Success(entity)
                }
                is Result.Error -> {
                    medicationDao.insertMedicationSchedule(schedule)
                    Result.Success(schedule)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.insertMedicationSchedule(schedule)
            Result.Success(schedule)
        }
    }
    
    /**
     * Returns Flow for reactive UI updates of patient schedules.
     */
    fun getSchedules(patientId: UUID): Flow<List<MedicationSchedule>> = 
        medicationDao.getMedicationSchedules(patientId)
    
    suspend fun getSchedule(id: UUID) = medicationDao.getSchedule(id)
    
    suspend fun updateSchedule(schedule: MedicationSchedule): Result<MedicationSchedule> {
        return try {
            val result = safeApiCall {
                apiService.updateSchedule(schedule.scheduleId.toString(), schedule.toDto())
            }
            
            medicationDao.updateMedicationSchedule(schedule)
            
            when (result) {
                is Result.Success -> Result.Success(schedule)
                is Result.Error -> Result.Success(schedule) // Updated locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.updateMedicationSchedule(schedule)
            Result.Success(schedule)
        }
    }
    
    suspend fun deleteSchedule(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteSchedule(id.toString())
            }
            
            medicationDao.deleteMedicationSchedule(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            medicationDao.deleteMedicationSchedule(id)
            Result.Success(Unit)
        }
    }
    
    /**
     * Update schedule status (DUE/TAKEN/OVERDUE).
     */
    suspend fun updateScheduleStatus(scheduleId: UUID, status: String): Result<MedicationSchedule> {
        return try {
            val result = safeApiCall {
                apiService.updateScheduleStatus(
                    scheduleId.toString(),
                    UpdateScheduleStatusRequest(status)
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    medicationDao.updateMedicationSchedule(entity)
                    Result.Success(entity)
                }
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }
    
    /**
     * Sync patient schedules from server.
     */
    suspend fun syncPatientSchedules(patientId: UUID): Result<List<MedicationSchedule>> {
        return try {
            val result = safeApiCall {
                apiService.getPatientSchedules(patientId.toString())
            }
            
            when (result) {
                is Result.Success -> {
                    val schedules = result.data.map { it.toEntity() }
                    schedules.forEach { medicationDao.insertMedicationSchedule(it) }
                    Result.Success(schedules)
                }
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }
    
    // ==================== Intake Records ====================
    
    suspend fun addIntake(record: MedicationIntakeRecord): Result<MedicationIntakeRecord> {
        return try {
            val result = safeApiCall {
                apiService.recordIntake(
                    RecordIntakeRequest(
                        scheduleId = record.scheduleId.toString(),
                        takenAt = record.takenAt
                    )
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    intakeDao.insertIntakeRecord(entity)
                    Result.Success(entity)
                }
                is Result.Error -> {
                    intakeDao.insertIntakeRecord(record)
                    Result.Success(record)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            intakeDao.insertIntakeRecord(record)
            Result.Success(record)
        }
    }
    
    suspend fun getIntakeRecords(id: UUID) = intakeDao.getIntake(id)
    
    suspend fun updateIntake(record: MedicationIntakeRecord): Result<MedicationIntakeRecord> {
        return try {
            val result = safeApiCall {
                apiService.updateIntake(record.intakeId.toString(), record.toDto())
            }
            
            intakeDao.updateIntakeRecord(record)
            
            when (result) {
                is Result.Success -> Result.Success(record)
                is Result.Error -> Result.Success(record) // Updated locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            intakeDao.updateIntakeRecord(record)
            Result.Success(record)
        }
    }
    
    suspend fun deleteIntake(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteIntake(id.toString())
            }
            
            intakeDao.deleteIntake(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            intakeDao.deleteIntake(id)
            Result.Success(Unit)
        }
    }
}
