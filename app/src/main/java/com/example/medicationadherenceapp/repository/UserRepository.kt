package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.FamilyDao
import com.example.medicationadherenceapp.data.local.dao.UserDao
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.FamilyPatientCrossRef
import com.example.medicationadherenceapp.data.local.entities.User
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for user and family operations. Responsible for composing
 * higher-level operations like linking a family member to a patient. Currently
 * wraps local DAOs; remote sync would be added here.
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val familyDao: FamilyDao
) {
    suspend fun addUser(user: User) = userDao.insertUser(user)
    suspend fun getPatientWithFamily(patientId: UUID) = userDao.getPatientWithFamily(patientId)
    suspend fun addFamilyMember(member: FamilyMember) = familyDao.insertFamilyMember(member)
    suspend fun linkFamilyToPatient(memberId: UUID, patientId: UUID) =
        familyDao.insertFamilyPatientCrossRef(FamilyPatientCrossRef(memberId, patientId))

    // User CRUD
    suspend fun getUser(id: UUID) = userDao.getUser(id)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(id: UUID) = userDao.deleteUser(id)

    // Family CRUD
    suspend fun getFamilyMember(id: UUID) = familyDao.getFamilyMember(id)
    suspend fun updateFamilyMember(member: FamilyMember) = familyDao.updateFamilyMember(member)
    suspend fun deleteFamilyMember(id: UUID) = familyDao.deleteFamilyMember(id)
    suspend fun unlinkFamilyFromPatient(memberId: UUID, patientId: UUID) =
        familyDao.deleteFamilyPatientCrossRef(memberId, patientId)
}
