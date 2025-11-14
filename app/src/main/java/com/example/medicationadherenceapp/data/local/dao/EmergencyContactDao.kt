package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.EmergencyContact
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO for emergency contacts. Exposes Flow for patient-specific contact lists
 * so contact UIs update when the DB changes.
 */
@Dao
interface EmergencyContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Query("SELECT * FROM EmergencyContact WHERE patientId = :patientId")
    fun getContacts(patientId: UUID): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM EmergencyContact WHERE contactId = :id")
    suspend fun getContact(id: UUID): EmergencyContact?

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("DELETE FROM EmergencyContact WHERE contactId = :id")
    suspend fun deleteContact(id: UUID)
}
