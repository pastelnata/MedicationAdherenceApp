package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.EmergencyContactDao
import com.example.medicationadherenceapp.data.local.entities.EmergencyContact
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository wrapping emergency contact DAO. Exposes Flow for contact lists
 * and suspend methods for CRUD. Keep business logic here (validation, mapping).
 */
class EmergencyContactRepository @Inject constructor(private val dao: EmergencyContactDao) {
    suspend fun addContact(contact: EmergencyContact) = dao.insertContact(contact)
    fun getContacts(patientId: UUID): Flow<List<EmergencyContact>> = dao.getContacts(patientId)

    suspend fun getContact(id: UUID) = dao.getContact(id)
    suspend fun updateContact(contact: EmergencyContact) = dao.updateContact(contact)
    suspend fun deleteContact(contact: EmergencyContact) = dao.deleteContact(contact)
    suspend fun deleteContact(id: UUID) = dao.deleteContact(id)
}
