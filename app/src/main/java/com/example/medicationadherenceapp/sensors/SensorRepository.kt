package com.example.medicationadherenceapp.sensors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing and managing sensor data.
 *
 * This repository follows the single-source-of-truth pattern and provides
 * a clean API for ViewModels to consume sensor data.
 *
 * Design notes:
 * - Exposes StateFlow for continuous sensor state
 * - Handles errors gracefully
 * - Provides methods to start/stop sensor monitoring
 * - Can be extended to persist sensor data to Room if needed
 */
@Singleton
class SensorRepository @Inject constructor(
    private val sensorManagerWrapper: SensorManagerWrapper
) {
    // StateFlows for each sensor type
    private val _stepCounterData = MutableStateFlow<SensorData?>(null)
    val stepCounterData: StateFlow<SensorData?> = _stepCounterData.asStateFlow()

    private val _accelerometerData = MutableStateFlow<SensorData?>(null)
    val accelerometerData: StateFlow<SensorData?> = _accelerometerData.asStateFlow()

    private val _lightSensorData = MutableStateFlow<SensorData?>(null)
    val lightSensorData: StateFlow<SensorData?> = _lightSensorData.asStateFlow()

    private val _sensorError = MutableStateFlow<String?>(null)
    val sensorError: StateFlow<String?> = _sensorError.asStateFlow()

    /**
     * Check if a specific sensor is available on the device.
     */
    fun isSensorAvailable(sensorType: SensorType): Boolean {
        return sensorManagerWrapper.isSensorAvailable(sensorType)
    }

    /**
     * Start monitoring a specific sensor.
     *
     * @param sensorType The type of sensor to monitor
     * @param scope The CoroutineScope in which to collect sensor data
     */
    fun startMonitoring(sensorType: SensorType, scope: CoroutineScope) {
        scope.launch {
            sensorManagerWrapper.getSensorData(sensorType)
                .catch { exception ->
                    _sensorError.value = "Error monitoring $sensorType: ${exception.message}"
                }
                .collect { data ->
                    when (sensorType) {
                        SensorType.STEP_COUNTER -> _stepCounterData.value = data
                        SensorType.ACCELEROMETER -> _accelerometerData.value = data
                        SensorType.LIGHT -> _lightSensorData.value = data
                    }
                }
        }
    }

    /**
     * Get the current step count from the step counter sensor.
     * Returns null if sensor is not available or no data has been received.
     */
    fun getCurrentStepCount(): Float? {
        return _stepCounterData.value?.values?.firstOrNull()
    }

    /**
     * Get the current accelerometer values (x, y, z).
     * Returns null if sensor is not available or no data has been received.
     */
    fun getCurrentAccelerometerValues(): FloatArray? {
        return _accelerometerData.value?.values
    }

    /**
     * Get the current light level in lux.
     * Returns null if sensor is not available or no data has been received.
     */
    fun getCurrentLightLevel(): Float? {
        return _lightSensorData.value?.values?.firstOrNull()
    }

    /**
     * Clear any sensor errors.
     */
    fun clearError() {
        _sensorError.value = null
    }

    /**
     * Get list of all available sensors on the device.
     */
    fun getAvailableSensorsList() = sensorManagerWrapper.getAvailableSensors()
}

