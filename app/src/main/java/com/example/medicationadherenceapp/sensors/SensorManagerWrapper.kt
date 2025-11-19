package com.example.medicationadherenceapp.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling all sensor operations.
 *
 * This class provides a clean API for sensor access, following lifecycle-aware patterns.
 * It exposes sensor data through reactive Flow streams and handles registration/unregistration
 * automatically.
 *
 * Key design points:
 * - Singleton to ensure a single SensorManager instance app-wide
 * - Uses Flow to expose sensor data reactively
 * - Automatically unregisters listeners when Flow is closed
 * - Thread-safe sensor registration/unregistration
 */
@Singleton
class SensorManagerWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /**
     * Check if a specific sensor type is available on the device.
     *
     * @param sensorType The type of sensor to check
     * @return true if the sensor is available, false otherwise
     */
    fun isSensorAvailable(sensorType: SensorType): Boolean {
        return when (sensorType) {
            SensorType.STEP_COUNTER -> getSensor(Sensor.TYPE_STEP_COUNTER) != null
            SensorType.ACCELEROMETER -> getSensor(Sensor.TYPE_ACCELEROMETER) != null
            SensorType.LIGHT -> getSensor(Sensor.TYPE_LIGHT) != null
        }
    }

    /**
     * Get a Flow of sensor data for a specific sensor type.
     *
     * The Flow will emit SensorData whenever the sensor reports new values.
     * When the Flow is collected, the sensor listener is registered automatically.
     * When the Flow collection is cancelled, the listener is unregistered.
     *
     * @param sensorType The type of sensor to observe
     * @param samplingPeriod The rate at which sensor events are delivered (default: SENSOR_DELAY_NORMAL)
     * @return Flow of SensorData
     */
    fun getSensorData(
        sensorType: SensorType,
        samplingPeriod: Int = SensorManager.SENSOR_DELAY_NORMAL
    ): Flow<SensorData> = callbackFlow {
        val sensor = when (sensorType) {
            SensorType.STEP_COUNTER -> getSensor(Sensor.TYPE_STEP_COUNTER)
            SensorType.ACCELEROMETER -> getSensor(Sensor.TYPE_ACCELEROMETER)
            SensorType.LIGHT -> getSensor(Sensor.TYPE_LIGHT)
        }

        if (sensor == null) {
            close(IllegalStateException("Sensor $sensorType not available"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val data = SensorData(
                        sensorType = sensorType,
                        values = it.values.clone(),
                        timestamp = it.timestamp,
                        accuracy = it.accuracy
                    )
                    // trySend is non-blocking; if buffer is full, it drops the value
                    trySend(data)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Optionally handle accuracy changes
            }
        }

        // Register the sensor listener
        val registered = sensorManager.registerListener(
            listener,
            sensor,
            samplingPeriod
        )

        if (!registered) {
            close(IllegalStateException("Failed to register sensor listener"))
            return@callbackFlow
        }

        // When the Flow is cancelled, unregister the listener
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    /**
     * Get a list of all available sensors on the device.
     *
     * @return List of all available Sensor objects
     */
    fun getAvailableSensors(): List<Sensor> {
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    private fun getSensor(type: Int): Sensor? {
        return sensorManager.getDefaultSensor(type)
    }
}

