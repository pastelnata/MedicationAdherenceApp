package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.PatientWithFamily
import com.example.medicationadherenceapp.data.local.entities.User
import java.util.UUID

/**
 * DAO for user CRUD and relation queries. Uses @Transaction for complex
 * queries that return relation projection objects (PatientWithFamily).
 * Keep user passwords safe in production: do not store plain text.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE userId = :id")
    suspend fun getUser(id: UUID): User?

    @Transaction
    @Query("SELECT * FROM User WHERE userId = :id")
    suspend fun getPatientWithFamily(id: UUID): PatientWithFamily

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM User WHERE userId = :id")
    suspend fun deleteUser(id: UUID)
}
