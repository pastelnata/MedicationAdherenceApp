package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.data.local.dao.FamilyDao
import com.example.medicationadherenceapp.data.local.dao.UserDao
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.FamilyPatientCrossRef
import com.example.medicationadherenceapp.data.local.entities.User
import com.example.medicationadherenceapp.data.remote.NetworkResult
import com.example.medicationadherenceapp.data.remote.RemoteDataSource
import com.example.medicationadherenceapp.data.remote.dto.LoginResponse
import com.example.medicationadherenceapp.data.remote.dto.RegisterResponse
import com.example.medicationadherenceapp.data.remote.dto.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

/**
 * Repository for user and family operations with authentication support.
 *
 * Handles:
 * - User authentication (login/register/logout)
 * - User and family member CRUD operations
 * - Local-first data access with optional remote sync
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val familyDao: FamilyDao,
    private val remoteDataSource: RemoteDataSource
) {
    // ==================== Authentication ====================

    /**
     * Login user with credentials.
     * On success, stores auth token and user info locally.
     */
    suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
        return when (val result = remoteDataSource.login(name, password)) {
            is NetworkResult.Success -> {
                val loginResponse = result.data

                // Store auth token
                loginResponse.token?.let { remoteDataSource.setAuthToken(it) }

                // Store user locally
                val user = User(
                    userId = UUID.fromString(loginResponse.userId),
                    name = loginResponse.name,
                    password = password, // Store for offline login
                    userType = UserType.valueOf(loginResponse.userType.uppercase())
                )
                userDao.insertUser(user)

                NetworkResult.Success(loginResponse)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Register new user account.
     */
    suspend fun register(
        name: String,
        password: String,
        userType: UserType
    ): NetworkResult<RegisterResponse> {
        return when (val result = remoteDataSource.register(
            name = name,
            password = password,
            userType = userType.name
        )) {
            is NetworkResult.Success -> {
                val registerResponse = result.data

                // Store user locally
                val user = User(
                    userId = UUID.fromString(registerResponse.userId),
                    name = registerResponse.name,
                    password = password,
                    userType = UserType.valueOf(registerResponse.userType.uppercase())
                )
                userDao.insertUser(user)

                NetworkResult.Success(registerResponse)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Logout current user.
     * Clears auth token and optionally clears local data.
     */
    suspend fun logout(): NetworkResult<Unit> {
        val result = remoteDataSource.logout()

        // Clear auth token regardless of network result
        remoteDataSource.setAuthToken(null)

        return result
    }

    /**
     * Offline login using locally stored credentials.
     * Falls back to this when network is unavailable.
     */
    suspend fun loginOffline(name: String, password: String): User? {
        val user = userDao.getUserByName(name)
        return if (user?.password == password) user else null
    }

    // ==================== Local-first operations ====================

    suspend fun addUser(user: User) = userDao.insertUser(user)

    suspend fun getPatientWithFamily(patientId: UUID) = userDao.getPatientWithFamily(patientId)

    suspend fun addFamilyMember(member: FamilyMember) = familyDao.insertFamilyMember(member)

    suspend fun linkFamilyToPatient(memberId: UUID, patientId: UUID) =
        familyDao.insertFamilyPatientCrossRef(FamilyPatientCrossRef(memberId, patientId))

    // ==================== User CRUD ====================

    suspend fun getUser(id: UUID) = userDao.getUser(id)

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        // TODO: Sync to remote
    }

    suspend fun deleteUser(id: UUID) {
        userDao.deleteUser(id)
        // TODO: Sync to remote
    }

    // ==================== Family CRUD ====================

    suspend fun getFamilyMember(id: UUID) = familyDao.getFamilyMember(id)

    suspend fun updateFamilyMember(member: FamilyMember) = familyDao.updateFamilyMember(member)

    suspend fun deleteFamilyMember(id: UUID) = familyDao.deleteFamilyMember(id)

    suspend fun unlinkFamilyFromPatient(memberId: UUID, patientId: UUID) =
        familyDao.deleteFamilyPatientCrossRef(memberId, patientId)

    // ==================== Network-based refresh operations ====================

    /**
     * Refresh user data from remote server.
     */
    suspend fun refreshUser(userId: UUID): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getUser(userId.toString())) {
            is NetworkResult.Success -> {
                // Update local cache with remote data
                val localUser = userDao.getUser(userId)
                if (localUser != null) {
                    val updatedUser = result.data.toEntity().copy(password = localUser.password)
                    userDao.insertUser(updatedUser)
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}

