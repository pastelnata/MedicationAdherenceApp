package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicationadherenceapp.sensors.SensorData
import com.example.medicationadherenceapp.sensors.SensorRepository
import com.example.medicationadherenceapp.sensors.SensorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Sensor monitoring screen.
 *
 * This ViewModel follows MVVM best practices:
 * - Exposes UI state via StateFlow (read-only to UI)
 * - Handles user intents (start/stop monitoring)
 * - Survives configuration changes
 * - Uses viewModelScope for coroutine lifecycle management
 *
 * UI state represents:
 * - Whether each sensor is available
 * - Whether monitoring is active for each sensor
 * - Current sensor readings
 * - Error messages
 */
@HiltViewModel
class SensorViewModel @Inject constructor(
    private val sensorRepository: SensorRepository
) : ViewModel() {

    // Sensor availability
    private val _isStepCounterAvailable = MutableStateFlow(false)
    val isStepCounterAvailable: StateFlow<Boolean> = _isStepCounterAvailable.asStateFlow()

    private val _isAccelerometerAvailable = MutableStateFlow(false)
    val isAccelerometerAvailable: StateFlow<Boolean> = _isAccelerometerAvailable.asStateFlow()

    private val _isLightSensorAvailable = MutableStateFlow(false)
    val isLightSensorAvailable: StateFlow<Boolean> = _isLightSensorAvailable.asStateFlow()

    // Monitoring state
    private val _isStepCounterMonitoring = MutableStateFlow(false)
    val isStepCounterMonitoring: StateFlow<Boolean> = _isStepCounterMonitoring.asStateFlow()

    private val _isAccelerometerMonitoring = MutableStateFlow(false)
    val isAccelerometerMonitoring: StateFlow<Boolean> = _isAccelerometerMonitoring.asStateFlow()

    private val _isLightSensorMonitoring = MutableStateFlow(false)
    val isLightSensorMonitoring: StateFlow<Boolean> = _isLightSensorMonitoring.asStateFlow()

    // Sensor data
    val stepCounterData: StateFlow<SensorData?> = sensorRepository.stepCounterData
    val accelerometerData: StateFlow<SensorData?> = sensorRepository.accelerometerData
    val lightSensorData: StateFlow<SensorData?> = sensorRepository.lightSensorData

    // Error state
    val sensorError: StateFlow<String?> = sensorRepository.sensorError

    init {
        checkSensorAvailability()
    }

    /**
     * Check which sensors are available on the device.
     */
    private fun checkSensorAvailability() {
        _isStepCounterAvailable.value = sensorRepository.isSensorAvailable(SensorType.STEP_COUNTER)
        _isAccelerometerAvailable.value = sensorRepository.isSensorAvailable(SensorType.ACCELEROMETER)
        _isLightSensorAvailable.value = sensorRepository.isSensorAvailable(SensorType.LIGHT)
    }

    /**
     * Toggle monitoring for step counter sensor.
     */
    fun toggleStepCounterMonitoring() {
        if (_isStepCounterMonitoring.value) {
            _isStepCounterMonitoring.value = false
            // Stopping is handled by cancellation of the coroutine scope
        } else {
            _isStepCounterMonitoring.value = true
            sensorRepository.startMonitoring(SensorType.STEP_COUNTER, viewModelScope)
        }
    }

    /**
     * Toggle monitoring for accelerometer sensor.
     */
    fun toggleAccelerometerMonitoring() {
        if (_isAccelerometerMonitoring.value) {
            _isAccelerometerMonitoring.value = false
        } else {
            _isAccelerometerMonitoring.value = true
            sensorRepository.startMonitoring(SensorType.ACCELEROMETER, viewModelScope)
        }
    }

    /**
     * Toggle monitoring for light sensor.
     */
    fun toggleLightSensorMonitoring() {
        if (_isLightSensorMonitoring.value) {
            _isLightSensorMonitoring.value = false
        } else {
            _isLightSensorMonitoring.value = true
            sensorRepository.startMonitoring(SensorType.LIGHT, viewModelScope)
        }
    }

    /**
     * Get formatted step count string.
     */
    fun getFormattedStepCount(): String {
        val count = sensorRepository.getCurrentStepCount()
        return count?.toInt()?.toString() ?: "N/A"
    }

    /**
     * Get formatted accelerometer values string.
     */
    fun getFormattedAccelerometerValues(): String {
        val values = sensorRepository.getCurrentAccelerometerValues()
        return if (values != null && values.size >= 3) {
            "X: %.2f, Y: %.2f, Z: %.2f".format(values[0], values[1], values[2])
        } else {
            "N/A"
        }
    }

    /**
     * Get formatted light level string.
     */
    fun getFormattedLightLevel(): String {
        val level = sensorRepository.getCurrentLightLevel()
        return level?.let { "%.2f lux".format(it) } ?: "N/A"
    }

    /**
     * Clear any error messages.
     */
    fun clearError() {
        sensorRepository.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModelScope will automatically cancel all coroutines,
        // which will trigger the Flow's awaitClose and unregister sensors
    }
}

