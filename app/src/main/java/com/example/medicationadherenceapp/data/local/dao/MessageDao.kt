package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.Message
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM Message WHERE receiverId = :patientId")
    fun getMessagesForPatient(patientId: UUID): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE messageId = :id")
    suspend fun getMessage(id: UUID): Message?

    @Query("DELETE FROM Message WHERE messageId = :id")
    suspend fun deleteMessage(id: UUID)
}
