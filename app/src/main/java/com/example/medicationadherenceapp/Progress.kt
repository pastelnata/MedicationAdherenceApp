package com.example.medicationadherenceapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarData(val day: String, val value: Float)

@Composable
fun ProgressComponent(modifier: Modifier = Modifier) {
    // Hardcoded data for the prototype
    val weeklyData = listOf(
        BarData("Mon", 75f),
        BarData("Tue", 100f),
        BarData("Wed", 50f),
        BarData("Thu", 100f),
        BarData("Fri", 75f),
        BarData("Sat", 100f),
        BarData("Sun", 75f)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProgressToggle()

            Spacer(modifier = Modifier.height(24.dp))

            BarChart(data = weeklyData)

            Spacer(modifier = Modifier.height(24.dp))

            SummaryRow()
        }
    }
}

@Composable
fun ProgressToggle() {
    var selectedIndex by remember { mutableIntStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = { selectedIndex = 0 },
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                containerColor = if (selectedIndex == 0) Color.White else Color.Transparent,
                contentColor = if (selectedIndex == 0) Color.Black else Color.Gray
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("Daily View", fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal)
        }

        TextButton(
            onClick = { selectedIndex = 1 },
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                containerColor = if (selectedIndex == 1) Color.White else Color.Transparent,
                contentColor = if (selectedIndex == 1) Color.Black else Color.Gray
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("Weekly Trends", fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal)
        }
    }
}


@Composable
fun BarChart(data: List<BarData>) {
    val maxValue = 100f

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("100", fontSize = 12.sp, color = Color.Gray)
                Text("75", fontSize = 12.sp, color = Color.Gray)
                Text("50", fontSize = 12.sp, color = Color.Gray)
                Text("25", fontSize = 12.sp, color = Color.Gray)
                Text("0", fontSize = 12.sp, color = Color.Gray)
            }

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { barData ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .fillMaxHeight(barData.value / maxValue)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Color(0xFF5B86E5))
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { barData ->
                Text(
                    text = barData.day,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        SummaryItem("85%", "This Week")
        SummaryItem("22/26", "Doses Taken")
        SummaryItem("4", "Missed")
    }
}

@Composable
fun SummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (label == "Missed") Color.Red else Color.Black
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ProgressComponentPreview() {
    ProgressComponent()
}



