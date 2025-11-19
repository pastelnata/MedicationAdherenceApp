package com.example.medicationadherenceapp.sensors

/**
 * Data class representing sensor data readings.
 *
 * @property sensorType The type of sensor that produced this data
 * @property values Array of sensor values (varies by sensor type)
 * @property timestamp The timestamp when the reading was taken
 * @property accuracy The accuracy of the sensor reading
 */
data class SensorData(
    val sensorType: SensorType,
    val values: FloatArray,
    val timestamp: Long,
    val accuracy: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SensorData

        if (sensorType != other.sensorType) return false
        if (!values.contentEquals(other.values)) return false
        if (timestamp != other.timestamp) return false
        if (accuracy != other.accuracy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sensorType.hashCode()
        result = 31 * result + values.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + accuracy
        return result
    }
}

