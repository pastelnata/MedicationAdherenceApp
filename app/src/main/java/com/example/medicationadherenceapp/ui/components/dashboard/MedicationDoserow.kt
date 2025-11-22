package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.let

// MedicationDoseRow.kt (for items in "Today's Medication")
@Composable
fun MedicationDoseRow(
    name: String,
    dosage: String,
    timeLabel: String?,
    onTapped: () -> Unit, // reserved for future click
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "$dosage${timeLabel?.let { " • $it" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
