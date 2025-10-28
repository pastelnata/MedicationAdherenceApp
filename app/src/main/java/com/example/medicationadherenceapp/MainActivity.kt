package com.example.medicationadherenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicationadherenceapp.ui.components.common.ScaffoldWithTopBar
import com.example.medicationadherenceapp.ui.components.dashboard.MainPage
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationAdherenceAppTheme {
                ScaffoldWithTopBar {
                    // if you want to insert data at app start, do it here using ViewModels
                    MainPage()
                }
            }
        }
    }
}
