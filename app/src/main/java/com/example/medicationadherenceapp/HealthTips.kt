package com.example.medicationadherenceapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class HealthTip(val title: String, val description: String)

//hard coded for now ig
val healthTips = listOf(
    HealthTip(
        title = "Take with Food",
        description = "Your blood pressure medication works best when taken with a meal to reduce stomach upset."
    ),
    HealthTip(
        title = "Stay Hydrated",
        description = "Drink plenty of water throughout the day, especially when taking diuretics."
    ),
    HealthTip(
        title = "Monitor Side Effects",
        description = "Contact your doctor if you experience dizziness, unusual fatigue, or swelling."
    )
)

@Preview
@Composable
fun HealthTips() {
    OutlinedCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                Icon(
                    painter = painterResource(DrawableIcons.LIGHT_BULB.id),
                    contentDescription = "Health Tips",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Health Tips",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                for (tip in healthTips) {
                    HealthTipCard(tip.title, tip.description)
                }
            }
        }
    }
}


@Composable
fun HealthTipCard(title: String, description: String) {
    OutlinedCard(
        modifier = Modifier.padding(vertical = 9.dp, horizontal= 16.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .height(90.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Health Tip",
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = description,
                modifier = Modifier.padding(start = 30.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
