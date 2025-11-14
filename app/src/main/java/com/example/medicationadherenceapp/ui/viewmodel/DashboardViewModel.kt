package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the dashboard screen.
 * Exposes a map of medication status counts so the UI can observe and render summaries.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val medicationRepository: com.example.medicationadherenceapp.repository.MedicationRepository,
    private val healthTipRepository: com.example.medicationadherenceapp.repository.HealthTipRepository
) : ViewModel() {
    // Backing mutable map of status counts. In a real app this would be derived
    // from a repository or combined flows (e.g., counts from Room). Keeping it
    // as a StateFlow makes it easy for Compose to collect and recompose when it changes.
    private val _statusCounts = MutableStateFlow(
        mapOf(
            com.example.medicationadherenceapp.MedStatus.OVERDUE to 1,
            com.example.medicationadherenceapp.MedStatus.DUE to 1,
            com.example.medicationadherenceapp.MedStatus.TAKEN to 1
        )
    )

    // Public read-only StateFlow so UI can observe counts but cannot modify them directly.
    val statusCounts: StateFlow<Map<com.example.medicationadherenceapp.MedStatus, Int>> = _statusCounts.asStateFlow()

    // Helper to set a specific count for a status. Creates a new map instance
    // to ensure StateFlow emits a new value and Compose recomposes.
    fun setStatusCount(status: com.example.medicationadherenceapp.MedStatus, count: Int) {
        val m = _statusCounts.value.toMutableMap()
        m[status] = count
        _statusCounts.value = m
    }

    // Increment a particular status count by `delta`. This keeps mutation
    // logic centralized in the ViewModel.
    fun incrementStatus(status: com.example.medicationadherenceapp.MedStatus, delta: Int = 1) {
        val m = _statusCounts.value.toMutableMap()
        m[status] = (m[status] ?: 0) + delta
        _statusCounts.value = m
    }

    // Reset counts back to zero. Useful for testing or user-triggered resets.
    fun resetCounts() {
        _statusCounts.value = mapOf(
            com.example.medicationadherenceapp.MedStatus.OVERDUE to 0,
            com.example.medicationadherenceapp.MedStatus.DUE to 0,
            com.example.medicationadherenceapp.MedStatus.TAKEN to 0
        )
    }
}
