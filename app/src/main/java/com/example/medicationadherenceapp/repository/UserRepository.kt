package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.FamilyDao
import com.example.medicationadherenceapp.data.local.dao.UserDao
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.FamilyPatientCrossRef
import com.example.medicationadherenceapp.data.local.entities.User
import com.example.medicationadherenceapp.data.remote.ApiService
import com.example.medicationadherenceapp.data.remote.dto.*
import com.example.medicationadherenceapp.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for user and family operations. Implements offline-first pattern:
 * - Read operations return data from local database (Room)
 * - Write operations sync with remote API and update local database
 * - Network errors are handled gracefully, allowing offline functionality
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val familyDao: FamilyDao,
    private val apiService: ApiService
) {
    
    // ==================== User CRUD Operations ====================
    
    /**
     * Add a new user. Syncs with remote API if available.
     */
    suspend fun addUser(user: User): Result<User> {
        return try {
            // Try to sync with remote first
            val result = safeApiCall {
                apiService.register(
                    RegisterRequest(
                        name = user.name,
                        password = user.password,
                        userType = user.userType.name
                    )
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val userEntity = result.data.toEntity()
                    userDao.insertUser(userEntity)
                    Result.Success(userEntity)
                }
                is Result.Error -> {
                    // If remote fails, save locally anyway
                    userDao.insertUser(user)
                    Result.Success(user)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            // Fallback to local-only
            userDao.insertUser(user)
            Result.Success(user)
        }
    }
    
    suspend fun getUser(id: UUID): User? = userDao.getUser(id)
    
    /**
     * Update user. Syncs with remote API if available.
     */
    suspend fun updateUser(user: User): Result<User> {
        return try {
            val result = safeApiCall {
                apiService.updateUser(user.userId.toString(), user.toDto())
            }
            
            when (result) {
                is Result.Success -> {
                    userDao.updateUser(user)
                    Result.Success(user)
                }
                is Result.Error -> {
                    // Update locally even if remote fails
                    userDao.updateUser(user)
                    Result.Success(user)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            userDao.updateUser(user)
            Result.Success(user)
        }
    }
    
    suspend fun deleteUser(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteUser(id.toString())
            }
            
            userDao.deleteUser(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            userDao.deleteUser(id)
            Result.Success(Unit)
        }
    }
    
    // ==================== Authentication ====================
    
    /**
     * Login user via API. Returns user data and auth token.
     */
    suspend fun login(name: String, password: String): Result<LoginResponse> {
        return safeApiCall {
            apiService.login(LoginRequest(name, password))
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return safeApiCall {
            apiService.logout()
        }
    }
    
    // ==================== Family Management ====================
    
    suspend fun getPatientWithFamily(patientId: UUID) = userDao.getPatientWithFamily(patientId)
    
    suspend fun addFamilyMember(member: FamilyMember): Result<FamilyMember> {
        return try {
            val result = safeApiCall {
                apiService.createFamilyMember(member.toDto())
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    familyDao.insertFamilyMember(entity)
                    Result.Success(entity)
                }
                is Result.Error -> {
                    familyDao.insertFamilyMember(member)
                    Result.Success(member)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            familyDao.insertFamilyMember(member)
            Result.Success(member)
        }
    }
    
    suspend fun linkFamilyToPatient(memberId: UUID, patientId: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.linkFamilyToPatient(
                    LinkFamilyRequest(memberId.toString(), patientId.toString())
                )
            }
            
            familyDao.insertFamilyPatientCrossRef(FamilyPatientCrossRef(memberId, patientId))
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Linked locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            familyDao.insertFamilyPatientCrossRef(FamilyPatientCrossRef(memberId, patientId))
            Result.Success(Unit)
        }
    }
    
    // Family CRUD
    suspend fun getFamilyMember(id: UUID) = familyDao.getFamilyMember(id)
    
    suspend fun updateFamilyMember(member: FamilyMember): Result<FamilyMember> {
        return try {
            val result = safeApiCall {
                apiService.updateFamilyMember(member.familyMemberId.toString(), member.toDto())
            }
            
            familyDao.updateFamilyMember(member)
            
            when (result) {
                is Result.Success -> Result.Success(member)
                is Result.Error -> Result.Success(member) // Updated locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            familyDao.updateFamilyMember(member)
            Result.Success(member)
        }
    }
    
    suspend fun deleteFamilyMember(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteFamilyMember(id.toString())
            }
            
            familyDao.deleteFamilyMember(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            familyDao.deleteFamilyMember(id)
            Result.Success(Unit)
        }
    }
    
    suspend fun unlinkFamilyFromPatient(memberId: UUID, patientId: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.unlinkFamilyFromPatient(memberId.toString(), patientId.toString())
            }
            
            familyDao.deleteFamilyPatientCrossRef(memberId, patientId)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Unlinked locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            familyDao.deleteFamilyPatientCrossRef(memberId, patientId)
            Result.Success(Unit)
        }
    }
    
    /**
     * Fetch patient's family from remote and update local database.
     */
    suspend fun syncPatientFamily(patientId: UUID): Result<List<FamilyMember>> {
        return try {
            val result = safeApiCall {
                apiService.getPatientFamily(patientId.toString())
            }
            
            when (result) {
                is Result.Success -> {
                    val familyMembers = result.data.map { it.toEntity() }
                    familyMembers.forEach { member ->
                        familyDao.insertFamilyMember(member)
                        familyDao.insertFamilyPatientCrossRef(
                            FamilyPatientCrossRef(member.familyMemberId, patientId)
                        )
                    }
                    Result.Success(familyMembers)
                }
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }
}
