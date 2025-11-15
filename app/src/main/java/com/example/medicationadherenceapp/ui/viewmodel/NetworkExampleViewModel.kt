package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicationadherenceapp.data.datastore.DataStoreManager
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.remote.SyncManager
import com.example.medicationadherenceapp.data.remote.dto.Result
import com.example.medicationadherenceapp.data.remote.interceptors.AuthInterceptor
import com.example.medicationadherenceapp.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Example ViewModel demonstrating network integration.
 * Shows how to use repositories with Result type and handle loading/error states.
 */
@HiltViewModel
class NetworkExampleViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val syncManager: SyncManager,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()
    
    // ==================== Example: Add Medication ====================
    
    fun addMedication(name: String, dosageMg: Float) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val medication = Medication(
                name = name,
                dosageMg = dosageMg
            )
            
            when (val result = medicationRepository.addMedication(medication)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success("Medication added successfully")
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(
                        result.message ?: "Failed to add medication"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }
    
    // ==================== Example: Sync Medications ====================
    
    fun syncMedications() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            when (val result = syncManager.syncMedications()) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(
                        "Synced ${result.data} medications"
                    )
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(
                        result.message ?: "Sync failed"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }
    
    // ==================== Example: Sync All Patient Data ====================
    
    fun syncPatientData(patientId: UUID) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Check network first
            if (!syncManager.isNetworkAvailable()) {
                _uiState.value = UiState.Error("No network connection")
                return@launch
            }
            
            when (val result = syncManager.syncPatientData(patientId)) {
                is Result.Success -> {
                    val syncResult = result.data
                    val message = buildString {
                        append("Sync completed:\n")
                        append("${syncResult.medicationsCount} medications\n")
                        append("${syncResult.schedulesCount} schedules\n")
                        append("${syncResult.intakesCount} intakes\n")
                        append("${syncResult.healthTipsCount} health tips\n")
                        append("${syncResult.familyMembersCount} family members")
                    }
                    _uiState.value = UiState.Success(message)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(
                        result.message ?: "Sync failed"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }
    
    // ==================== Example: Update Schedule Status ====================
    
    fun markMedicationTaken(scheduleId: UUID) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            when (val result = medicationRepository.updateScheduleStatus(
                scheduleId, 
                "TAKEN"
            )) {
                is Result.Success -> {
                    _uiState.value = UiState.Success("Medication marked as taken")
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(
                        result.message ?: "Failed to update status"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }
    
    // ==================== Example: Save Auth Token ====================
    
    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            dataStoreManager.savePreference(AuthInterceptor.AUTH_TOKEN_KEY, token)
        }
    }
    
    // ==================== Example: Handle Multiple Operations ====================
    
    fun performBulkOperation(patientId: UUID) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // Sync medications
                val medicationsResult = syncManager.syncMedications()
                if (medicationsResult is Result.Error) {
                    _uiState.value = UiState.Error("Failed to sync medications")
                    return@launch
                }
                
                // Sync schedules
                val schedulesResult = syncManager.syncPatientSchedules(patientId)
                if (schedulesResult is Result.Error) {
                    _uiState.value = UiState.Error("Failed to sync schedules")
                    return@launch
                }
                
                // Sync health tips
                val tipsResult = syncManager.syncHealthTips()
                if (tipsResult is Result.Error) {
                    _uiState.value = UiState.Error("Failed to sync health tips")
                    return@launch
                }
                
                _uiState.value = UiState.Success("All data synced successfully")
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// UI State sealed class
sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
