@file:Suppress("unused")
package com.example.medicationadherenceapp.data.remote

import android.util.Log
import com.example.medicationadherenceapp.data.remote.dto.*
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source that wraps ApiService calls with proper error handling.
 *
 * This class handles:
 * - Network errors (IOException)
 * - HTTP errors (non-2xx responses)
 * - Parsing errors
 * - Authentication token management
 *
 * All methods return NetworkResult<T> for type-safe error handling in repositories.
 */
@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "RemoteDataSource"
    }

    // Token storage - in production, use encrypted SharedPreferences or DataStore
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun getAuthToken(): String? = authToken

    /**
     * Generic function to execute API calls with error handling.
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    NetworkResult.Success(body.data)
                } else {
                    NetworkResult.Error(
                        message = body?.message ?: "Unknown error occurred",
                        code = response.code()
                    )
                }
            } else {
                val errorMessage = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error parsing error response"
                }
                NetworkResult.Error(
                    message = errorMessage,
                    code = response.code()
                )
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error", e)
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            NetworkResult.Error("Unexpected error: ${e.message}")
        }
    }

    private fun requireToken(): String {
        return authToken ?: throw IllegalStateException("Auth token not set")
    }

    // ==================== Health Check ====================

    suspend fun healthCheck(): NetworkResult<String> {
        return safeApiCall { apiService.healthCheck() }
    }

    // ==================== Authentication ====================

    suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
        return safeApiCall {
            apiService.login(LoginRequest(name, password))
        }
    }

    suspend fun register(
        name: String,
        password: String,
        userType: String
    ): NetworkResult<RegisterResponse> {
        return safeApiCall {
            apiService.register(RegisterRequest(name, password, userType))
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        return safeApiCall {
            apiService.logout("Bearer ${requireToken()}")
        }
    }

    // ==================== User Management ====================

    suspend fun getUser(userId: String): NetworkResult<UserDto> {
        return safeApiCall {
            apiService.getUser(userId, "Bearer ${requireToken()}")
        }
    }

    suspend fun updateUser(userId: String, user: UserDto): NetworkResult<UserDto> {
        return safeApiCall {
            apiService.updateUser(userId, user, "Bearer ${requireToken()}")
        }
    }

    suspend fun deleteUser(userId: String): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteUser(userId, "Bearer ${requireToken()}")
        }
    }

    // ==================== Medications ====================

    suspend fun getMedications(): NetworkResult<List<MedicationDto>> {
        return safeApiCall {
            apiService.getMedications("Bearer ${requireToken()}")
        }
    }

    suspend fun getMedication(medicationId: String): NetworkResult<MedicationDto> {
        return safeApiCall {
            apiService.getMedication(medicationId, "Bearer ${requireToken()}")
        }
    }

    suspend fun createMedication(
        name: String,
        dosageMg: Float
    ): NetworkResult<MedicationDto> {
        return safeApiCall {
            apiService.createMedication(
                CreateMedicationRequest(name, dosageMg),
                "Bearer ${requireToken()}"
            )
        }
    }

    suspend fun updateMedication(
        medicationId: String,
        medication: MedicationDto
    ): NetworkResult<MedicationDto> {
        return safeApiCall {
            apiService.updateMedication(
                medicationId,
                medication,
                "Bearer ${requireToken()}"
            )
        }
    }

    suspend fun deleteMedication(medicationId: String): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteMedication(medicationId, "Bearer ${requireToken()}")
        }
    }

    // ==================== Medication Schedules ====================

    suspend fun getSchedules(patientId: String): NetworkResult<List<MedicationScheduleDto>> {
        return safeApiCall {
            apiService.getSchedules(patientId, "Bearer ${requireToken()}")
        }
    }

    suspend fun getSchedule(scheduleId: String): NetworkResult<MedicationScheduleDto> {
        return safeApiCall {
            apiService.getSchedule(scheduleId, "Bearer ${requireToken()}")
        }
    }

    suspend fun createSchedule(
        patientId: String,
        medicationId: String,
        scheduledTime: Long
    ): NetworkResult<MedicationScheduleDto> {
        return safeApiCall {
            apiService.createSchedule(
                CreateScheduleRequest(patientId, medicationId, scheduledTime),
                "Bearer ${requireToken()}"
            )
        }
    }

    suspend fun updateSchedule(
        scheduleId: String,
        schedule: MedicationScheduleDto
    ): NetworkResult<MedicationScheduleDto> {
        return safeApiCall {
            apiService.updateSchedule(
                scheduleId,
                schedule,
                "Bearer ${requireToken()}"
            )
        }
    }

    suspend fun deleteSchedule(scheduleId: String): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteSchedule(scheduleId, "Bearer ${requireToken()}")
        }
    }

    // ==================== Intake Records ====================

    suspend fun getIntakeRecords(scheduleId: String): NetworkResult<List<MedicationIntakeRecordDto>> {
        return safeApiCall {
            apiService.getIntakeRecords(scheduleId, "Bearer ${requireToken()}")
        }
    }

    suspend fun recordIntake(
        scheduleId: String,
        takenAt: Long
    ): NetworkResult<MedicationIntakeRecordDto> {
        return safeApiCall {
            apiService.recordIntake(
                RecordIntakeRequest(scheduleId, takenAt),
                "Bearer ${requireToken()}"
            )
        }
    }

    suspend fun deleteIntake(intakeId: String): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteIntake(intakeId, "Bearer ${requireToken()}")
        }
    }

    // ==================== Health Tips ====================

    suspend fun getHealthTips(limit: Int? = null): NetworkResult<HealthTipsResponse> {
        return safeApiCall {
            apiService.getHealthTips("Bearer ${requireToken()}", limit)
        }
    }

    suspend fun getHealthTip(tipId: String): NetworkResult<HealthTipDto> {
        return safeApiCall {
            apiService.getHealthTip(tipId, "Bearer ${requireToken()}")
        }
    }

    // ==================== Sync Operations ====================

    suspend fun syncSchedules(
        schedules: List<MedicationScheduleDto>
    ): NetworkResult<List<MedicationScheduleDto>> {
        return safeApiCall {
            apiService.syncSchedules(schedules, "Bearer ${requireToken()}")
        }
    }

    suspend fun syncIntakes(
        intakes: List<MedicationIntakeRecordDto>
    ): NetworkResult<List<MedicationIntakeRecordDto>> {
        return safeApiCall {
            apiService.syncIntakes(intakes, "Bearer ${requireToken()}")
        }
    }
}

