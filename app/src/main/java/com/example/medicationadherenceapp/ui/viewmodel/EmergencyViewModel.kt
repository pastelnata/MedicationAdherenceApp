package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicationadherenceapp.repository.EmergencyContactRepository
import com.example.medicationadherenceapp.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for emergency features using shake detection.
 * When user shakes device, can trigger emergency alert to contacts.
 */
@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val sensorRepository: SensorRepository,
    private val emergencyContactRepository: EmergencyContactRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<EmergencyUiState>(EmergencyUiState.Idle)
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()
    
    // Shake detection enabled flag
    private val _shakeDetectionEnabled = MutableStateFlow(false)
    val shakeDetectionEnabled: StateFlow<Boolean> = _shakeDetectionEnabled.asStateFlow()
    
    // Emergency alert triggered event
    private val _emergencyTriggered = MutableSharedFlow<Unit>()
    val emergencyTriggered: SharedFlow<Unit> = _emergencyTriggered.asSharedFlow()
    
    init {
        checkSensorAvailability()
    }
    
    /**
     * Check if shake detection is available.
     */
    private fun checkSensorAvailability() {
        if (!sensorRepository.isShakeDetectionSupported()) {
            _uiState.value = EmergencyUiState.SensorNotAvailable(
                "Accelerometer not available on this device"
            )
        }
    }
    
    /**
     * Enable shake-to-alert feature.
     */
    fun enableShakeDetection() {
        if (!sensorRepository.isShakeDetectionSupported()) {
            _uiState.value = EmergencyUiState.SensorNotAvailable(
                "Shake detection not supported"
            )
            return
        }
        
        viewModelScope.launch {
            _shakeDetectionEnabled.value = true
            _uiState.value = EmergencyUiState.Listening
            
            sensorRepository.observeShakeEvents()
                .catch { e ->
                    _uiState.value = EmergencyUiState.Error(
                        "Shake detection error: ${e.message}"
                    )
                }
                .collect {
                    handleShakeDetected()
                }
        }
    }
    
    /**
     * Disable shake-to-alert feature.
     */
    fun disableShakeDetection() {
        _shakeDetectionEnabled.value = false
        _uiState.value = EmergencyUiState.Idle
    }
    
    /**
     * Handle shake detected event.
     */
    private suspend fun handleShakeDetected() {
        _uiState.value = EmergencyUiState.ShakeDetected
        
        // Emit event for UI to show confirmation dialog
        _emergencyTriggered.emit(Unit)
        
        // Reset to listening after brief delay
        kotlinx.coroutines.delay(1000)
        if (_shakeDetectionEnabled.value) {
            _uiState.value = EmergencyUiState.Listening
        }
    }
    
    /**
     * Trigger emergency alert (after user confirms).
     * In a real app, this would:
     * - Send SMS to emergency contacts
     * - Make phone calls
     * - Share location
     * - Log the emergency event
     */
    suspend fun confirmEmergencyAlert() {
        _uiState.value = EmergencyUiState.AlertTriggered
        
        // TODO: Implement emergency actions
        // - Get emergency contacts
        // - Send notifications
        // - Log emergency event
        
        kotlinx.coroutines.delay(2000)
        if (_shakeDetectionEnabled.value) {
            _uiState.value = EmergencyUiState.Listening
        } else {
            _uiState.value = EmergencyUiState.Idle
        }
    }
    
    /**
     * Cancel emergency alert.
     */
    fun cancelEmergencyAlert() {
        if (_shakeDetectionEnabled.value) {
            _uiState.value = EmergencyUiState.Listening
        } else {
            _uiState.value = EmergencyUiState.Idle
        }
    }
}

/**
 * UI state for emergency screen.
 */
sealed class EmergencyUiState {
    data object Idle : EmergencyUiState()
    data object Listening : EmergencyUiState()
    data object ShakeDetected : EmergencyUiState()
    data object AlertTriggered : EmergencyUiState()
    data class SensorNotAvailable(val message: String) : EmergencyUiState()
    data class Error(val message: String) : EmergencyUiState()
}
