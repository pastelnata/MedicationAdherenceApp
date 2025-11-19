package com.example.medicationadherenceapp.ui.components.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medicationadherenceapp.ui.viewmodel.SensorViewModel

/**
 * Main sensor screen composable.
 *
 * This screen displays sensor information and allows users to monitor
 * various device sensors that can help track health and activity data.
 *
 * Design:
 * - Shows which sensors are available on the device
 * - Allows toggling sensor monitoring on/off
 * - Displays real-time sensor readings
 * - Shows error messages when sensors fail
 */
@Composable
fun SensorScreen(
    viewModel: SensorViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel
    val isStepCounterAvailable by viewModel.isStepCounterAvailable.collectAsState()
    val isAccelerometerAvailable by viewModel.isAccelerometerAvailable.collectAsState()
    val isLightSensorAvailable by viewModel.isLightSensorAvailable.collectAsState()

    val isStepCounterMonitoring by viewModel.isStepCounterMonitoring.collectAsState()
    val isAccelerometerMonitoring by viewModel.isAccelerometerMonitoring.collectAsState()
    val isLightSensorMonitoring by viewModel.isLightSensorMonitoring.collectAsState()

    val stepCounterData by viewModel.stepCounterData.collectAsState()
    val accelerometerData by viewModel.accelerometerData.collectAsState()
    val lightSensorData by viewModel.lightSensorData.collectAsState()

    val sensorError by viewModel.sensorError.collectAsState()

    // Show error snackbar if there's an error
    sensorError?.let { error ->
        LaunchedEffect(error) {
            // In a real app, you'd show a Snackbar here
            // For now, we'll just clear it after showing
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Sensor Monitoring",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Monitor device sensors to track activity and health patterns",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Step Counter Card
        SensorCard(
            title = "Step Counter",
            description = "Track daily steps for activity monitoring",
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            isAvailable = isStepCounterAvailable,
            isMonitoring = isStepCounterMonitoring,
            onToggleMonitoring = { viewModel.toggleStepCounterMonitoring() },
            currentValue = if (stepCounterData != null) {
                viewModel.getFormattedStepCount()
            } else {
                "No data"
            }
        )

        // Accelerometer Card
        SensorCard(
            title = "Accelerometer",
            description = "Detect movement and activity patterns",
            icon = Icons.Default.Speed,
            isAvailable = isAccelerometerAvailable,
            isMonitoring = isAccelerometerMonitoring,
            onToggleMonitoring = { viewModel.toggleAccelerometerMonitoring() },
            currentValue = if (accelerometerData != null) {
                viewModel.getFormattedAccelerometerValues()
            } else {
                "No data"
            }
        )

        // Light Sensor Card
        SensorCard(
            title = "Light Sensor",
            description = "Monitor ambient light levels",
            icon = Icons.Default.Lightbulb,
            isAvailable = isLightSensorAvailable,
            isMonitoring = isLightSensorMonitoring,
            onToggleMonitoring = { viewModel.toggleLightSensorMonitoring() },
            currentValue = if (lightSensorData != null) {
                viewModel.getFormattedLightLevel()
            } else {
                "No data"
            }
        )

        // Info card about sensor usage
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Sensor data helps track your activity patterns and can provide insights for medication adherence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Reusable card component for displaying sensor information.
 */
@Composable
private fun SensorCard(
    title: String,
    description: String,
    icon: ImageVector,
    isAvailable: Boolean,
    isMonitoring: Boolean,
    onToggleMonitoring: () -> Unit,
    currentValue: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Availability status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (isAvailable) "Available" else "Not Available",
                    tint = if (isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isAvailable) "Sensor Available" else "Sensor Not Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            // Current value display
            if (isAvailable && isMonitoring) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Current Reading",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = currentValue,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Toggle button
            Button(
                onClick = onToggleMonitoring,
                enabled = isAvailable,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isMonitoring) "Stop Monitoring" else "Start Monitoring",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMonitoring) "Stop Monitoring" else "Start Monitoring"
                )
            }
        }
    }
}

