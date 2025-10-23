package com.example.medicationadherenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicationadherenceapp.userInterface.MainRoute
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import com.example.medicationadherenceapp.userInterface.MainPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationAdherenceAppTheme {
                ScaffoldWithTopBar {
                    MainRoute()
                }
            }
        }
    }
}
