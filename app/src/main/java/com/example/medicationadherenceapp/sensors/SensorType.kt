package com.example.medicationadherenceapp.sensors

/**
 * Enum representing the types of sensors used in the app.
 * This helps maintain type safety and clear sensor identification.
 */
enum class SensorType {
    STEP_COUNTER,      // Tracks step count for daily activity monitoring
    ACCELEROMETER,     // Detects movement and potential fall detection
    LIGHT              // Monitors ambient light to infer user activity patterns
}

