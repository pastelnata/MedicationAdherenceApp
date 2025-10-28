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

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(med: Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationSchedule(schedule: MedicationSchedule)

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
