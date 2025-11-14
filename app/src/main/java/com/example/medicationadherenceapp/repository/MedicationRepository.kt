package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for medication domain. This class mediates access to the local
 * DAO layer and is the place to add remote synchronization in the future.
 *
 * Current behavior (local-first):
 *  - Read methods return Room Flow<T> so UI layers can observe data.
 *  - Write methods are suspend functions that call DAO suspend methods.
 *
 * If you later add a RemoteDataSource, this repository would implement the
 * sync strategy (NetworkBoundResource / explicit refreshes) and merge remote
 * results into Room.
 */
@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val intakeDao: MedicationIntakeDao
) {
    suspend fun addMedication(med: Medication) = medicationDao.insertMedication(med)
    suspend fun scheduleMedication(schedule: MedicationSchedule) = medicationDao.insertMedicationSchedule(schedule)
    fun getSchedules(patientId: UUID): Flow<List<MedicationSchedule>> = medicationDao.getMedicationSchedules(patientId)
    suspend fun addIntake(record: MedicationIntakeRecord) = intakeDao.insertIntakeRecord(record)

    // Medication CRUD
    suspend fun getMedication(id: UUID) = medicationDao.getMedication(id)
    suspend fun updateMedication(med: Medication) = medicationDao.updateMedication(med)
    suspend fun deleteMedication(id: UUID) = medicationDao.deleteMedication(id)

    // Schedule CRUD
    suspend fun getSchedule(id: UUID) = medicationDao.getSchedule(id)
    suspend fun updateSchedule(schedule: MedicationSchedule) = medicationDao.updateMedicationSchedule(schedule)
    suspend fun deleteSchedule(id: UUID) = medicationDao.deleteMedicationSchedule(id)

    // Intake CRUD
    suspend fun getIntakeRecords(id: UUID) = intakeDao.getIntake(id)
    suspend fun updateIntake(record: MedicationIntakeRecord) = intakeDao.updateIntakeRecord(record)
    suspend fun deleteIntake(id: UUID) = intakeDao.deleteIntake(id)
}
