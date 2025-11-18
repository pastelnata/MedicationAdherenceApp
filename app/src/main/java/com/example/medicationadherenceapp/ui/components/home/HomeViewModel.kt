package com.example.medicationadherenceapp.ui.components.home

import androidx.lifecycle.ViewModel
import comp.example.medicationadherenceapp.data.Medication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    init {
        // Load initial data
        _medications.value = listOf(
            Medication("Aspirin"),
            Medication("Ibuprofen"),
            Medication("Acetaminophen")
        )
    }
}