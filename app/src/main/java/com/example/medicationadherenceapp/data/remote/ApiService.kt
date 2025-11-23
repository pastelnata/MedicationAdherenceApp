@file:Suppress("unused")
package com.example.medicationadherenceapp.data.remote

import com.example.medicationadherenceapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for the Medication Adherence App.
 *
 * This interface defines all REST API endpoints for:
 * - User authentication and management
 * - Medication CRUD operations
 * - Schedule management
 * - Intake record tracking
 * - Health tips retrieval
 *
 * All methods are suspend functions for coroutine-based async execution.
 * Uses Response<T> wrapper to allow proper error handling in RemoteDataSource.
 */
interface ApiService {

    // ==================== Health Check ====================

    @GET("health")
    suspend fun healthCheck(): Response<ApiResponse<String>>

    // ==================== Authentication ====================

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // ==================== User Management ====================

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserDto>>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: UserDto,
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserDto>>

    @DELETE("users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // ==================== Medications ====================

    @GET("medications")
    suspend fun getMedications(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationDto>>>

    @GET("medications/{medicationId}")
    suspend fun getMedication(
        @Path("medicationId") medicationId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationDto>>

    @POST("medications")
    suspend fun createMedication(
        @Body request: CreateMedicationRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationDto>>

    @PUT("medications/{medicationId}")
    suspend fun updateMedication(
        @Path("medicationId") medicationId: String,
        @Body medication: MedicationDto,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationDto>>

    @DELETE("medications/{medicationId}")
    suspend fun deleteMedication(
        @Path("medicationId") medicationId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // ==================== Medication Schedules ====================

    @GET("schedules")
    suspend fun getSchedules(
        @Query("patientId") patientId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationScheduleDto>>>

    @GET("schedules/{scheduleId}")
    suspend fun getSchedule(
        @Path("scheduleId") scheduleId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationScheduleDto>>

    @POST("schedules")
    suspend fun createSchedule(
        @Body request: CreateScheduleRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationScheduleDto>>

    @PUT("schedules/{scheduleId}")
    suspend fun updateSchedule(
        @Path("scheduleId") scheduleId: String,
        @Body schedule: MedicationScheduleDto,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationScheduleDto>>

    @DELETE("schedules/{scheduleId}")
    suspend fun deleteSchedule(
        @Path("scheduleId") scheduleId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // ==================== Intake Records ====================

    @GET("intakes")
    suspend fun getIntakeRecords(
        @Query("scheduleId") scheduleId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationIntakeRecordDto>>>

    @POST("intakes")
    suspend fun recordIntake(
        @Body request: RecordIntakeRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationIntakeRecordDto>>

    @DELETE("intakes/{intakeId}")
    suspend fun deleteIntake(
        @Path("intakeId") intakeId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    // ==================== Health Tips ====================

    @GET("health-tips")
    suspend fun getHealthTips(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<HealthTipsResponse>>

    @GET("health-tips/{tipId}")
    suspend fun getHealthTip(
        @Path("tipId") tipId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<HealthTipDto>>

    // ==================== Sync Operations ====================

    @POST("sync/schedules")
    suspend fun syncSchedules(
        @Body schedules: List<MedicationScheduleDto>,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationScheduleDto>>>

    @POST("sync/intakes")
    suspend fun syncIntakes(
        @Body intakes: List<MedicationIntakeRecordDto>,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationIntakeRecordDto>>>
}