package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicationadherenceapp.MedStatus

@Composable
fun MedStatusSummary(
    statusCounts: Map<MedStatus, Int>
) {
    Row {
        MedStatus.entries.forEach { status ->
            val baseColor = when (status) {
                MedStatus.OVERDUE -> Color.Red
                MedStatus.DUE -> Color(0xFFFFA000)
                MedStatus.TAKEN -> Color(0xFF388E3C)
            }
            val borderColor = baseColor.copy(alpha = 0.4f)
            OutlinedCard(
                modifier = Modifier.weight(1f).padding(6.dp).height(100.dp),
                colors = CardDefaults.outlinedCardColors(),
                border = BorderStroke(2.dp, borderColor)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = statusCounts[status]?.toString() ?: "0",
                        color = baseColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = status.statusLabel,
                        color = baseColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MedStatusSummaryRowPreview() {
    MedStatusSummary(
        statusCounts = mapOf(
            MedStatus.OVERDUE to 1,
            MedStatus.DUE to 1,
            MedStatus.TAKEN to 1
        )
    )
}
