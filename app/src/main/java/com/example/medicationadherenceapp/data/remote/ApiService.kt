package com.example.medicationadherenceapp.data.remote

import com.example.medicationadherenceapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface defining all network endpoints.
 * All methods are suspend functions for coroutine support.
 * 
 * Base URL should be configured in NetworkModule.
 * Example: https://api.medicationapp.com/v1/
 */
interface ApiService {
    
    // ==================== Health Check ====================
    
    @GET("health")
    suspend fun healthCheck(): Response<Unit>
    
    // ==================== User Authentication ====================
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    
    // ==================== User Management ====================
    
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserDto>
    
    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: UserDto
    ): Response<UserDto>
    
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): Response<Unit>
    
    // ==================== Family Management ====================
    
    @GET("family/{familyMemberId}")
    suspend fun getFamilyMember(@Path("familyMemberId") familyMemberId: String): Response<FamilyMemberDto>
    
    @POST("family")
    suspend fun createFamilyMember(@Body familyMember: FamilyMemberDto): Response<FamilyMemberDto>
    
    @PUT("family/{familyMemberId}")
    suspend fun updateFamilyMember(
        @Path("familyMemberId") familyMemberId: String,
        @Body familyMember: FamilyMemberDto
    ): Response<FamilyMemberDto>
    
    @DELETE("family/{familyMemberId}")
    suspend fun deleteFamilyMember(@Path("familyMemberId") familyMemberId: String): Response<Unit>
    
    @POST("family/link")
    suspend fun linkFamilyToPatient(@Body request: LinkFamilyRequest): Response<Unit>
    
    @DELETE("family/{familyMemberId}/patient/{patientId}")
    suspend fun unlinkFamilyFromPatient(
        @Path("familyMemberId") familyMemberId: String,
        @Path("patientId") patientId: String
    ): Response<Unit>
    
    @GET("users/{patientId}/family")
    suspend fun getPatientFamily(@Path("patientId") patientId: String): Response<List<FamilyMemberDto>>
    
    // ==================== Medication Management ====================
    
    @GET("medications")
    suspend fun getAllMedications(): Response<List<MedicationDto>>
    
    @GET("medications/{medicationId}")
    suspend fun getMedication(@Path("medicationId") medicationId: String): Response<MedicationDto>
    
    @POST("medications")
    suspend fun createMedication(@Body request: CreateMedicationRequest): Response<MedicationDto>
    
    @PUT("medications/{medicationId}")
    suspend fun updateMedication(
        @Path("medicationId") medicationId: String,
        @Body medication: MedicationDto
    ): Response<MedicationDto>
    
    @DELETE("medications/{medicationId}")
    suspend fun deleteMedication(@Path("medicationId") medicationId: String): Response<Unit>
    
    // ==================== Medication Schedules ====================
    
    @GET("patients/{patientId}/schedules")
    suspend fun getPatientSchedules(@Path("patientId") patientId: String): Response<List<MedicationScheduleDto>>
    
    @GET("schedules/{scheduleId}")
    suspend fun getSchedule(@Path("scheduleId") scheduleId: String): Response<MedicationScheduleDto>
    
    @POST("schedules")
    suspend fun createSchedule(@Body request: CreateScheduleRequest): Response<MedicationScheduleDto>
    
    @PUT("schedules/{scheduleId}")
    suspend fun updateSchedule(
        @Path("scheduleId") scheduleId: String,
        @Body schedule: MedicationScheduleDto
    ): Response<MedicationScheduleDto>
    
    @PATCH("schedules/{scheduleId}/status")
    suspend fun updateScheduleStatus(
        @Path("scheduleId") scheduleId: String,
        @Body request: UpdateScheduleStatusRequest
    ): Response<MedicationScheduleDto>
    
    @DELETE("schedules/{scheduleId}")
    suspend fun deleteSchedule(@Path("scheduleId") scheduleId: String): Response<Unit>
    
    // ==================== Medication Intake Records ====================
    
    @GET("intakes/{intakeId}")
    suspend fun getIntakeRecord(@Path("intakeId") intakeId: String): Response<MedicationIntakeDto>
    
    @GET("schedules/{scheduleId}/intakes")
    suspend fun getScheduleIntakes(@Path("scheduleId") scheduleId: String): Response<List<MedicationIntakeDto>>
    
    @POST("intakes")
    suspend fun recordIntake(@Body request: RecordIntakeRequest): Response<MedicationIntakeDto>
    
    @PUT("intakes/{intakeId}")
    suspend fun updateIntake(
        @Path("intakeId") intakeId: String,
        @Body intake: MedicationIntakeDto
    ): Response<MedicationIntakeDto>
    
    @DELETE("intakes/{intakeId}")
    suspend fun deleteIntake(@Path("intakeId") intakeId: String): Response<Unit>
    
    // ==================== Health Tips ====================
    
    @GET("health-tips")
    suspend fun getHealthTips(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<HealthTipDto>>
    
    @GET("health-tips/{tipId}")
    suspend fun getHealthTip(@Path("tipId") tipId: String): Response<HealthTipDto>
    
    @POST("health-tips")
    suspend fun createHealthTip(@Body request: CreateHealthTipRequest): Response<HealthTipDto>
    
    @PUT("health-tips/{tipId}")
    suspend fun updateHealthTip(
        @Path("tipId") tipId: String,
        @Body tip: HealthTipDto
    ): Response<HealthTipDto>
    
    @DELETE("health-tips/{tipId}")
    suspend fun deleteHealthTip(@Path("tipId") tipId: String): Response<Unit>
    
    // ==================== Sync Endpoints ====================
    
    /**
     * Sync all data for a specific patient. Returns updated records
     * that have been modified since the provided timestamp.
     */
    @GET("sync/patient/{patientId}")
    suspend fun syncPatientData(
        @Path("patientId") patientId: String,
        @Query("since") lastSyncTimestamp: Long
    ): Response<SyncResponse>
}