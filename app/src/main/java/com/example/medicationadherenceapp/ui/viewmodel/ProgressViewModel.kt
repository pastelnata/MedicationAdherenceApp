package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Simple ViewModel for the Progress component to hoist UI selection state.
 * Keeps track of a selected index so the UI can show the active progress bucket.
 */
@HiltViewModel
class ProgressViewModel @Inject constructor(private val medicationRepository: com.example.medicationadherenceapp.repository.MedicationRepository) : ViewModel() {
    // Holds which tab/index is selected; default is 1. UI collects this StateFlow
    // and updates visuals accordingly.
    private val _selectedIndex = MutableStateFlow(1)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    // Single writer: expose a method to change the selected index.
    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }
}
