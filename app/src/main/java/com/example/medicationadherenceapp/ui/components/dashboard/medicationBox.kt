package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.text.isNotBlank

@Composable
fun MedicationAlertCard(
    modifier: Modifier = Modifier,
    medName: String,
    dosage: String,
    frequency: String,
    minutesOverdue: Int = 0,
    instructions: String = "",
    showOverdueBanner: Boolean = minutesOverdue > 0,   // NEW
    statusLabel: String? = if (minutesOverdue > 0) "Overdue" else null, // NEW
    onConfirmedTaken: () -> Unit,
    onSkip: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, if (showOverdueBanner) Color(0xFFFFA726) else MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Show the orange overdue banner only for urgent cards
            if (showOverdueBanner) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFF6D00))
                    Spacer(Modifier.width(8.dp))
                    Text("This medication is overdue", color = Color(0xFFBF360C))
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(medName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("$dosage • $frequency", style = MaterialTheme.typography.bodyMedium)
                }
                if (statusLabel != null) {
                    AssistChip(onClick = {}, label = { Text(statusLabel) })
                }
            }

            // Show "X min overdue" only for urgent
            if (showOverdueBanner) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFEF6C00))
                    Spacer(Modifier.width(8.dp))
                    Text("$minutesOverdue min overdue", color = Color(0xFFEF6C00))
                }
            }

            if (instructions.isNotBlank()) {
                Text(instructions, style = MaterialTheme.typography.bodyMedium)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726), contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.CheckCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text("I took this medication")
                }
                OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                    Text("I can't take this now")
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Confirm") },
            text = { Text("Are you sure you took this medication?") },
            confirmButton = {
                TextButton(onClick = { showConfirm = false; onConfirmedTaken() }) { Text("Yes, confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
