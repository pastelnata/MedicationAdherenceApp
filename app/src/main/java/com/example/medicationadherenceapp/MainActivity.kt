package com.example.medicationadherenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicationadherenceapp.userInterface.MainRoute
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import com.example.medicationadherenceapp.userInterface.DoseUi
import com.example.medicationadherenceapp.userInterface.MainPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationAdherenceAppTheme {
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
                MainRoute()
            }
        }
    }
}
