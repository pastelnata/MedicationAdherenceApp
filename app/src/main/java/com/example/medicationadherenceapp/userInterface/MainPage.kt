package com.example.medicationadherenceapp.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicationadherenceapp.*  // for DrawableIcons, MedStatus, MedStatusSummary, HealthTips, etc.

@Composable
fun MainPage(
    urgentDoses: List<DoseUi>,
    todayDoses: List<DoseUi>,
    onConfirmTaken: (DoseUi) -> Unit,
    onSkip: (DoseUi) -> Unit,
    takenCount: Int,                                    // NEW: show taken count
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            MedStatusSummary(
                statusCounts = mapOf(
                    MedStatus.OVERDUE to urgentDoses.size,
                    MedStatus.DUE to todayDoses.size,
                    MedStatus.TAKEN to takenCount
                )
            )
        }

        // ---- Needs Immediate Attention Med Cards ----
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(DrawableIcons.ALARM.id),
                    contentDescription = "Alert",
                    modifier = Modifier.size(30.dp).padding(end = 4.dp),
                    tint = Color(0xFFD32F2F),
                )
                HeaderText("Needs Immediate Attention")
            }
        }

        items(urgentDoses, key = { it.id }) { dose ->
            MedicationAlertCard(
                medName = dose.name,
                dosage = dose.dosage,
                frequency = dose.frequency ?: "",
                minutesOverdue = dose.minutesOverdue ?: 0,
                instructions = dose.instructions ?: "",
                showOverdueBanner = true,
                statusLabel = "Overdue",
                onConfirmedTaken = { onConfirmTaken(dose) },
                onSkip = { onSkip(dose) }
            )
        }

        // ---- Today's Medication header ----
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderText("Today's Medication")
                OutlinedCard {
                    Text(
                        text = "$takenCount/${takenCount + todayDoses.size + urgentDoses.size} taken",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(todayDoses, key = { it.id }) { dose ->
            MedicationAlertCard(
                medName = dose.name,
                dosage = dose.dosage,
                frequency = dose.frequency ?: "",
                minutesOverdue = 0,
                instructions = dose.instructions ?: "",
                showOverdueBanner = false,
                statusLabel = null,
                onConfirmedTaken = { onConfirmTaken(dose) },
                onSkip = { onSkip(dose) }
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            HealthTips()
        }
    }
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview(showBackground = true)
@Composable
private fun MainPagePreview() {
    val urgent = listOf(
        DoseUi("1", "Metformin", "500mg", "Twice daily", minutesOverdue = 60, instructions = "Take with meals")
    )
    val today = listOf(
        DoseUi("2", "Vitamin D", "40 µg", timeLabel = "09:00"),
        DoseUi("3", "Ibuprofen", "200 mg", timeLabel = "14:00")
    )
    ScaffoldWithTopBar {
        MainPage(
            urgentDoses = urgent,
            todayDoses = today,
            onConfirmTaken = {},
            onSkip = {},
            takenCount = 0
        )
    }
}


