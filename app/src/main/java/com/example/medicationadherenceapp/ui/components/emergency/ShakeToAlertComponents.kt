package com.example.medicationadherenceapp.ui.components.emergency

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.medicationadherenceapp.ui.viewmodel.EmergencyUiState
import com.example.medicationadherenceapp.ui.viewmodel.EmergencyViewModel
import kotlinx.coroutines.launch

/**
 * Emergency shake detection card.
 * Shows status and allows enabling/disabling shake-to-alert feature.
 */
@Composable
fun ShakeToAlertCard(
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val shakeEnabled by viewModel.shakeDetectionEnabled.collectAsState()
    val scope = rememberCoroutineScope()
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    // Observe emergency triggered events
    LaunchedEffect(Unit) {
        viewModel.emergencyTriggered.collect {
            showConfirmDialog = true
        }
    }
    
    // Auto-enable shake detection when screen is visible
    LifecycleResumeEffect(Unit) {
        viewModel.enableShakeDetection()
        onPauseOrDispose {
            viewModel.disableShakeDetection()
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState is EmergencyUiState.ShakeDetected) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Shake to Alert",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Switch(
                    checked = shakeEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            viewModel.enableShakeDetection()
                        } else {
                            viewModel.disableShakeDetection()
                        }
                    }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Content based on state
            when (uiState) {
                is EmergencyUiState.Idle -> {
                    Text(
                        text = "Shake detection is disabled",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Enable to activate emergency alerts by shaking your device",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                is EmergencyUiState.Listening -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Listening",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Listening for shake gesture",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Shake your device to trigger emergency alert",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                is EmergencyUiState.ShakeDetected -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Shake Detected",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Shake detected!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                is EmergencyUiState.AlertTriggered -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Emergency alert sent!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                is EmergencyUiState.SensorNotAvailable -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SensorsOff,
                            contentDescription = "Not Available",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = (uiState as EmergencyUiState.SensorNotAvailable).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                is EmergencyUiState.Error -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = (uiState as EmergencyUiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    
    // Emergency confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                showConfirmDialog = false
                viewModel.cancelEmergencyAlert()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "Emergency",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Emergency Alert Detected")
            },
            text = {
                Text("Do you want to send an emergency alert to your contacts?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.confirmEmergencyAlert()
                            showConfirmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Send Alert")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.cancelEmergencyAlert()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
