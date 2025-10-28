package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MedicationIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakeRecord(record: MedicationIntakeRecord)

    @Query("SELECT * FROM MedicationIntakeRecord WHERE scheduleId = :scheduleId")
    fun getIntakeRecords(scheduleId: UUID): Flow<List<MedicationIntakeRecord>>

    @Query("SELECT * FROM MedicationIntakeRecord WHERE intakeId = :id")
    suspend fun getIntake(id: UUID): MedicationIntakeRecord?

    @Update
    suspend fun updateIntakeRecord(record: MedicationIntakeRecord)

    @Query("DELETE FROM MedicationIntakeRecord WHERE intakeId = :id")
    suspend fun deleteIntake(id: UUID)
}
