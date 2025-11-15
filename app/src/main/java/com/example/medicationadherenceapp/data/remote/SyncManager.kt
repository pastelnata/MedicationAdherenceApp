package com.example.medicationadherenceapp.data.remote

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.medicationadherenceapp.data.datastore.DataStoreManager
import com.example.medicationadherenceapp.data.remote.dto.Result
import com.example.medicationadherenceapp.repository.HealthTipRepository
import com.example.medicationadherenceapp.repository.MedicationRepository
import com.example.medicationadherenceapp.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized sync manager for coordinating data synchronization between
 * local database and remote API. Handles full sync and incremental sync.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val userRepository: UserRepository,
    private val medicationRepository: MedicationRepository,
    private val healthTipRepository: HealthTipRepository,
    private val dataStoreManager: DataStoreManager
) {
    
    companion object {
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }
    
    /**
     * Perform full sync for a patient. Fetches all data from server.
     */
    suspend fun syncPatientData(patientId: UUID): Result<SyncResult> {
        return try {
            val lastSyncTime = dataStoreManager.getPreference(LAST_SYNC_TIMESTAMP, 0L).first()
            
            val syncResponse = safeApiCall {
                apiService.syncPatientData(patientId.toString(), lastSyncTime)
            }
            
            when (syncResponse) {
                is Result.Success -> {
                    val data = syncResponse.data
                    
                    // Update local database with synced data
                    data.medications?.forEach { medicationDto ->
                        medicationRepository.addMedication(medicationDto.toEntity())
                    }
                    
                    data.schedules?.forEach { scheduleDto ->
                        medicationRepository.scheduleMedication(scheduleDto.toEntity())
                    }
                    
                    data.intakes?.forEach { intakeDto ->
                        medicationRepository.addIntake(intakeDto.toEntity())
                    }
                    
                    data.healthTips?.forEach { tipDto ->
                        healthTipRepository.addTip(tipDto.toEntity())
                    }
                    
                    data.familyMembers?.forEach { familyDto ->
                        userRepository.addFamilyMember(familyDto.toEntity())
                    }
                    
                    // Update last sync timestamp
                    dataStoreManager.savePreference(LAST_SYNC_TIMESTAMP, data.lastSyncTimestamp)
                    
                    Result.Success(
                        SyncResult(
                            success = true,
                            medicationsCount = data.medications?.size ?: 0,
                            schedulesCount = data.schedules?.size ?: 0,
                            intakesCount = data.intakes?.size ?: 0,
                            healthTipsCount = data.healthTips?.size ?: 0,
                            familyMembersCount = data.familyMembers?.size ?: 0
                        )
                    )
                }
                is Result.Error -> Result.Error(
                    syncResponse.exception,
                    "Sync failed: ${syncResponse.message}"
                )
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, "Sync error: ${e.message}")
        }
    }
    
    /**
     * Sync individual data types
     */
    suspend fun syncMedications(): Result<Int> {
        return when (val result = medicationRepository.syncMedications()) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.exception, result.message)
            is Result.Loading -> Result.Loading
        }
    }
    
    suspend fun syncPatientSchedules(patientId: UUID): Result<Int> {
        return when (val result = medicationRepository.syncPatientSchedules(patientId)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.exception, result.message)
            is Result.Loading -> Result.Loading
        }
    }
    
    suspend fun syncHealthTips(limit: Int = 50): Result<Int> {
        return when (val result = healthTipRepository.syncHealthTips(limit)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.exception, result.message)
            is Result.Loading -> Result.Loading
        }
    }
    
    suspend fun syncPatientFamily(patientId: UUID): Result<Int> {
        return when (val result = userRepository.syncPatientFamily(patientId)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.exception, result.message)
            is Result.Loading -> Result.Loading
        }
    }
    
    /**
     * Check network connectivity before sync
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null
    }
}

/**
 * Result of a sync operation
 */
data class SyncResult(
    val success: Boolean,
    val medicationsCount: Int = 0,
    val schedulesCount: Int = 0,
    val intakesCount: Int = 0,
    val healthTipsCount: Int = 0,
    val familyMembersCount: Int = 0
)
