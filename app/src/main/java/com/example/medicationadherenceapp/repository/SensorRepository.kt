package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.sensors.AccelerometerData
import com.example.medicationadherenceapp.data.sensors.AppSensorManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for sensor data. Provides a clean abstraction over sensor access
 * and can add business logic like step goal tracking, activity patterns, etc.
 */
@Singleton
class SensorRepository @Inject constructor(
    private val sensorManager: AppSensorManager
) {
    
    /**
     * Check if step counting is available on this device.
     */
    fun isStepCountingSupported(): Boolean {
        return sensorManager.isStepCounterAvailable()
    }
    
    /**
     * Check if shake detection is available on this device.
     */
    fun isShakeDetectionSupported(): Boolean {
        return sensorManager.isAccelerometerAvailable()
    }
    
    /**
     * Get step count as a Flow.
     * Returns total steps since device boot.
     */
    fun getStepCount(): Flow<Int> {
        return sensorManager.getStepCountFlow()
    }
    
    /**
     * Get shake detection events as a Flow.
     * Emits Unit when device is shaken.
     */
    fun observeShakeEvents(): Flow<Unit> {
        return sensorManager.getShakeDetectionFlow()
    }
    
    /**
     * Get raw accelerometer data as a Flow.
     */
    fun getAccelerometerData(): Flow<AccelerometerData> {
        return sensorManager.getAccelerometerDataFlow()
    }
    
    /**
     * Get list of all available sensors on this device.
     * Useful for debugging or showing capabilities to user.
     */
    fun getAvailableSensors(): List<String> {
        return sensorManager.getAvailableSensors().map { sensor ->
            "${sensor.name} (${sensor.type})"
        }
    }
}
