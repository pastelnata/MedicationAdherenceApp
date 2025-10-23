package com.example.medicationadherenceapp.userInterface

// DoseUi.kt
data class DoseUi(
    val id: String,
    val name: String,
    val dosage: String,
    val frequency: String? = null,
    val timeLabel: String? = null,
    val minutesOverdue: Int? = null,
    val instructions: String? = null
)
