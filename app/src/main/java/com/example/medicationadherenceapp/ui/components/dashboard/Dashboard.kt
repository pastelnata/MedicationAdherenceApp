package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.medicationadherenceapp.ui.components.common.ScaffoldWithTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.medicationadherenceapp.ui.components.dashboard.MedicationAlertCard
import com.example.medicationadherenceapp.ui.components.dashboard.MedicationDoseRow
import com.example.medicationadherenceapp.ui.viewmodel.DashboardViewModel


@Composable
fun MainPage(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayDoses by viewModel.todayDoses.collectAsState()

    // pick one overdue dose (if any) for the "Needs Immediate Attention" box
    val urgentDose = todayDoses.firstOrNull { (it.minutesOverdue ?: 0) > 0 }
    // the rest go into "Today's Medication"
    val otherDoses = todayDoses.filterNot { it.id == urgentDose?.id }

    val statusCounts by viewModel.statusCounts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // hard coded for now if
        MedStatusSummary(
                statusCounts = statusCounts
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(_root_ide_package_.com.example.medicationadherenceapp.DrawableIcons.ALARM.id),
                contentDescription = "Alert",
                modifier = Modifier.size(30.dp).padding(end = 4.dp),
                tint = Color(0xFFD32F2F)
            )
            HeaderText("Needs Immediate Attention")
        }

        // medication boxes
        urgentDose?.let { dose ->
            Spacer(modifier = Modifier.padding(top = 8.dp))

            MedicationAlertCard(
                medName = dose.name,
                dosage = dose.dosage,
                frequency = dose.frequency ?: "",
                minutesOverdue = dose.minutesOverdue ?: 0,
                instructions = dose.instructions ?: "",
                onConfirmedTaken = { viewModel.markDoseTaken(dose.id) },
                onSkip = { viewModel.skipDose(dose.id) }
            )
        }


        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderText("Today's Medication")
            OutlinedCard {
                Text(
                    // hard coded for now as well
                    text = "1/4 taken",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // today's medication boxes
        if (otherDoses.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = 8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                otherDoses.forEach { dose ->
                    MedicationAlertCard(
                        medName = dose.name,
                        dosage = dose.dosage,
                        frequency = dose.frequency ?: "",
                        minutesOverdue = dose.minutesOverdue ?: 0,
                        instructions = dose.instructions ?: "",
                        onConfirmedTaken = { viewModel.markDoseTaken(dose.id) },
                        onSkip = { viewModel.skipDose(dose.id) }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.padding(8.dp))

        HealthTips()
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

@Preview
@Composable
fun MainPagePreview() {
    ScaffoldWithTopBar {
        MainPage()
    }
}
