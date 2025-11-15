package com.example.medicationadherenceapp.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Centralized sensor manager for the medication adherence app.
 * Handles sensor lifecycle, registration, and data streaming.
 * 
 * Sensors used:
 * - Step Counter: Track daily activity for health monitoring
 * - Accelerometer: Detect shake gestures for emergency alerts
 */
@Singleton
class AppSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    companion object {
        private const val TAG = "AppSensorManager"
        private const val SHAKE_THRESHOLD = 15f // m/s²
        private const val SHAKE_TIME_LAPSE = 500 // ms
    }
    
    // Track last shake time to prevent duplicate detections
    private var lastShakeTime = 0L
    
    /**
     * Check if step counter sensor is available on this device.
     */
    fun isStepCounterAvailable(): Boolean {
        return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
    }
    
    /**
     * Check if accelerometer sensor is available on this device.
     */
    fun isAccelerometerAvailable(): Boolean {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }
    
    /**
     * Get all available sensors on this device.
     */
    fun getAvailableSensors(): List<Sensor> {
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }
    
    /**
     * Stream step count data as a Flow.
     * Emits total steps since device boot.
     * 
     * Note: This requires android.permission.ACTIVITY_RECOGNITION on Android 10+
     */
    fun getStepCountFlow(): Flow<Int> = callbackFlow {
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (stepSensor == null) {
            Log.e(TAG, "Step counter sensor not available")
            close()
            return@callbackFlow
        }
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values[0].toInt()
                    trySend(steps)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d(TAG, "Step counter accuracy changed: $accuracy")
            }
        }
        
        val registered = sensorManager.registerListener(
            listener,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        if (!registered) {
            Log.e(TAG, "Failed to register step counter listener")
            close()
            return@callbackFlow
        }
        
        Log.d(TAG, "Step counter listener registered")
        
        awaitClose {
            sensorManager.unregisterListener(listener)
            Log.d(TAG, "Step counter listener unregistered")
        }
    }
    
    /**
     * Stream shake detection events as a Flow.
     * Emits Unit when a shake gesture is detected.
     * 
     * Useful for:
     * - Emergency alerts
     * - Quick medication log
     * - Panic button
     */
    fun getShakeDetectionFlow(): Flow<Unit> = callbackFlow {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        if (accelerometer == null) {
            Log.e(TAG, "Accelerometer sensor not available")
            close()
            return@callbackFlow
        }
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    
                    // Calculate acceleration magnitude (excluding gravity)
                    val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
                    
                    if (acceleration > SHAKE_THRESHOLD) {
                        val currentTime = System.currentTimeMillis()
                        
                        // Prevent duplicate shake detections
                        if (currentTime - lastShakeTime > SHAKE_TIME_LAPSE) {
                            lastShakeTime = currentTime
                            Log.d(TAG, "Shake detected! Acceleration: $acceleration m/s²")
                            trySend(Unit)
                        }
                    }
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d(TAG, "Accelerometer accuracy changed: $accuracy")
            }
        }
        
        val registered = sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME // Higher frequency for better shake detection
        )
        
        if (!registered) {
            Log.e(TAG, "Failed to register accelerometer listener")
            close()
            return@callbackFlow
        }
        
        Log.d(TAG, "Accelerometer listener registered")
        
        awaitClose {
            sensorManager.unregisterListener(listener)
            Log.d(TAG, "Accelerometer listener unregistered")
        }
    }
    
    /**
     * Stream raw accelerometer data as a Flow.
     * Emits AccelerometerData with x, y, z values.
     */
    fun getAccelerometerDataFlow(): Flow<AccelerometerData> = callbackFlow {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        if (accelerometer == null) {
            Log.e(TAG, "Accelerometer sensor not available")
            close()
            return@callbackFlow
        }
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val data = AccelerometerData(
                        x = event.values[0],
                        y = event.values[1],
                        z = event.values[2],
                        timestamp = event.timestamp
                    )
                    trySend(data)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d(TAG, "Accelerometer accuracy changed: $accuracy")
            }
        }
        
        val registered = sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        if (!registered) {
            Log.e(TAG, "Failed to register accelerometer listener")
            close()
            return@callbackFlow
        }
        
        Log.d(TAG, "Accelerometer data listener registered")
        
        awaitClose {
            sensorManager.unregisterListener(listener)
            Log.d(TAG, "Accelerometer data listener unregistered")
        }
    }
}

/**
 * Data class for accelerometer readings.
 */
data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)
