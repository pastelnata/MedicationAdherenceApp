package com.example.medicationadherenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationAdherenceAppTheme {
                // Use the app navigation graph as the root composable
                com.example.medicationadherenceapp.ui.navigation.NavGraph()
            }
        }
    }
}
