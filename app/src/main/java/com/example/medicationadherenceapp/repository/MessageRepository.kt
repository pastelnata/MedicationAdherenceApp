package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.MessageDao
import com.example.medicationadherenceapp.data.local.entities.Message
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MessageRepository @Inject constructor(private val dao: MessageDao) {
    suspend fun sendMessage(message: Message) = dao.insertMessage(message)
    fun getMessages(patientId: UUID): Flow<List<Message>> = dao.getMessagesForPatient(patientId)

    suspend fun getMessage(id: UUID) = dao.getMessage(id)

    suspend fun deleteMessage(id: UUID) = dao.deleteMessage(id)
}
