package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicationadherenceapp.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for health tracking features using step counter sensor.
 * Tracks daily steps and calculates activity goals.
 */
@HiltViewModel
class HealthTrackingViewModel @Inject constructor(
    private val sensorRepository: SensorRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<HealthTrackingUiState>(HealthTrackingUiState.Idle)
    val uiState: StateFlow<HealthTrackingUiState> = _uiState.asStateFlow()
    
    // Step count data
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()
    
    // Daily step goal (default: 10,000 steps)
    private val _stepGoal = MutableStateFlow(10000)
    val stepGoal: StateFlow<Int> = _stepGoal.asStateFlow()
    
    // Progress percentage (0-100)
    val stepProgress: StateFlow<Int> = combine(_stepCount, _stepGoal) { steps, goal ->
        if (goal > 0) ((steps.toFloat() / goal) * 100).toInt().coerceIn(0, 100)
        else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    // Initial step count (from device boot)
    private var initialStepCount = 0
    private var hasSetInitialCount = false
    
    init {
        checkSensorAvailability()
    }
    
    /**
     * Check if step counting is available on this device.
     */
    private fun checkSensorAvailability() {
        if (!sensorRepository.isStepCountingSupported()) {
            _uiState.value = HealthTrackingUiState.SensorNotAvailable(
                "Step counter not available on this device"
            )
        }
    }
    
    /**
     * Start tracking steps. Should be called when screen becomes active.
     */
    fun startStepTracking() {
        if (!sensorRepository.isStepCountingSupported()) {
            _uiState.value = HealthTrackingUiState.SensorNotAvailable(
                "Step counter not available"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = HealthTrackingUiState.Tracking
            
            sensorRepository.getStepCount()
                .catch { e ->
                    _uiState.value = HealthTrackingUiState.Error(
                        "Failed to track steps: ${e.message}"
                    )
                }
                .collect { totalSteps ->
                    // On first reading, set baseline
                    if (!hasSetInitialCount) {
                        initialStepCount = totalSteps
                        hasSetInitialCount = true
                    }
                    
                    // Calculate steps since app started
                    val dailySteps = totalSteps - initialStepCount
                    _stepCount.value = dailySteps.coerceAtLeast(0)
                }
        }
    }
    
    /**
     * Stop tracking steps. Should be called when screen becomes inactive.
     */
    fun stopStepTracking() {
        _uiState.value = HealthTrackingUiState.Idle
        // Flow will be cancelled when viewModelScope is cancelled
    }
    
    /**
     * Reset daily step count.
     */
    fun resetDailySteps() {
        hasSetInitialCount = false
        _stepCount.value = 0
    }
    
    /**
     * Update step goal.
     */
    fun setStepGoal(goal: Int) {
        if (goal > 0) {
            _stepGoal.value = goal
        }
    }
    
    /**
     * Get motivational message based on progress.
     */
    fun getMotivationalMessage(): String {
        return when (stepProgress.value) {
            in 0..25 -> "Great start! Keep moving! 🚶"
            in 26..50 -> "You're halfway there! 💪"
            in 51..75 -> "Almost there! Keep it up! 🏃"
            in 76..99 -> "So close to your goal! 🎯"
            else -> "Goal achieved! Amazing! 🎉"
        }
    }
}

/**
 * UI state for health tracking screen.
 */
sealed class HealthTrackingUiState {
    data object Idle : HealthTrackingUiState()
    data object Tracking : HealthTrackingUiState()
    data class SensorNotAvailable(val message: String) : HealthTrackingUiState()
    data class Error(val message: String) : HealthTrackingUiState()
}
