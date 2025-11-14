package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO for medication-related tables. Provides suspend methods for writes
 * (inserts/updates/deletes) and Flow-returning queries for reactive reads.
 *
 * Best practice pattern used:
 *  - use suspend DAO methods for writes so callers (repositories/ViewModels)
 *    can execute them from coroutines
 *  - expose Flow<T> for queries the UI should observe so Room pushes updates
 *    when the underlying table changes
 */
@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(med: Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationSchedule(schedule: MedicationSchedule)

    // Observe all schedules for the given patient. Returning Flow<List<...>>
    // lets ViewModels collect and expose a StateFlow to the UI; Room will
    // automatically emit when schedules change.
    @Query("SELECT * FROM MedicationSchedule WHERE patientId = :patientId")
    fun getMedicationSchedules(patientId: UUID): Flow<List<MedicationSchedule>>

    @Query("SELECT * FROM Medication WHERE medicationId = :id")
    suspend fun getMedication(id: UUID): Medication?

    @Update
    suspend fun updateMedication(med: Medication)

    @Query("DELETE FROM Medication WHERE medicationId = :id")
    suspend fun deleteMedication(id: UUID)

    @Query("SELECT * FROM MedicationSchedule WHERE scheduleId = :id")
    suspend fun getSchedule(id: UUID): MedicationSchedule?

    @Update
    suspend fun updateMedicationSchedule(schedule: MedicationSchedule)


    @Query("DELETE FROM MedicationSchedule WHERE scheduleId = :id")
    suspend fun deleteMedicationSchedule(id: UUID)
}
