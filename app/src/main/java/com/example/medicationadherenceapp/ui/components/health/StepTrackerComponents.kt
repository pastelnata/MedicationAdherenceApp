package com.example.medicationadherenceapp.ui.components.health

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
import com.example.medicationadherenceapp.ui.viewmodel.HealthTrackingUiState
import com.example.medicationadherenceapp.ui.viewmodel.HealthTrackingViewModel

/**
 * Step tracking card that displays daily step count and progress.
 * Uses step counter sensor to track physical activity.
 */
@Composable
fun StepTrackerCard(
    viewModel: HealthTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stepCount by viewModel.stepCount.collectAsState()
    val stepGoal by viewModel.stepGoal.collectAsState()
    val progress by viewModel.stepProgress.collectAsState()
    
    // Start tracking when screen is visible, stop when not
    LifecycleResumeEffect(Unit) {
        viewModel.startStepTracking()
        onPauseOrDispose {
            viewModel.stopStepTracking()
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsWalk,
                    contentDescription = "Steps",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Daily Steps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Content based on state
            when (uiState) {
                is HealthTrackingUiState.Idle -> {
                    Text("Step tracking paused")
                }
                
                is HealthTrackingUiState.Tracking -> {
                    // Step count
                    Text(
                        text = "$stepCount",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "of $stepGoal steps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        text = "$progress% of daily goal",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Motivational message
                    Text(
                        text = viewModel.getMotivationalMessage(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                is HealthTrackingUiState.SensorNotAvailable -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = (uiState as HealthTrackingUiState.SensorNotAvailable).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                is HealthTrackingUiState.Error -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = (uiState as HealthTrackingUiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { viewModel.resetDailySteps() }) {
                    Text("Reset")
                }
                
                Spacer(Modifier.width(8.dp))
                
                TextButton(onClick = { 
                    // Show dialog to set custom goal
                    // TODO: Implement goal setting dialog
                }) {
                    Text("Set Goal")
                }
            }
        }
    }
}

/**
 * Compact step counter for dashboard.
 */
@Composable
fun StepCounterChip(
    viewModel: HealthTrackingViewModel = hiltViewModel()
) {
    val stepCount by viewModel.stepCount.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    LifecycleResumeEffect(Unit) {
        viewModel.startStepTracking()
        onPauseOrDispose {
            viewModel.stopStepTracking()
        }
    }
    
    if (uiState is HealthTrackingUiState.Tracking) {
        AssistChip(
            onClick = { /* Navigate to full health tracking screen */ },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = "Steps",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("$stepCount steps")
                }
            },
            leadingIcon = null
        )
    }
}
