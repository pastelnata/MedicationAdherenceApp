package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.medicationadherenceapp.ui.components.dashboard.DoseUi

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

    // Today's doses to drive the medication cards on the dashboard
    private val _todayDoses = MutableStateFlow<List<DoseUi>>(emptyList())
    val todayDoses: StateFlow<List<DoseUi>> = _todayDoses

    init {
        // TODO: replace this with real data from medicationRepository later
        _todayDoses.value = listOf(
            DoseUi(
                id = "1",
                name = "Metformin",
                dosage = "500 mg",
                frequency = "Once daily",
                timeLabel = "08:00",
                minutesOverdue = 30,
                instructions = "Take with breakfast."
            ),
            DoseUi(
                id = "2",
                name = "Vitamin D",
                dosage = "1000 IU",
                frequency = "Once daily",
                timeLabel = "12:00",
                minutesOverdue = 0,
                instructions = "You can take this with water."
            )
        )

        // Initialize statusCounts based on todayDoses
        val overdue = _todayDoses.value.count { (it.minutesOverdue ?: 0) > 0 }
        val dueSoon = _todayDoses.value.size - overdue
        _statusCounts.value = mapOf(
            com.example.medicationadherenceapp.MedStatus.OVERDUE to overdue,
            com.example.medicationadherenceapp.MedStatus.DUE to dueSoon,
            com.example.medicationadherenceapp.MedStatus.TAKEN to 0
        )
    }


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

    // ADD: dashboard medication handlers

    fun markDoseTaken(id: String) {
        // simple behavior for now: remove it from today's list and increment TAKEN count
        val current = _todayDoses.value
        val dose = current.firstOrNull { it.id == id } ?: return
        _todayDoses.value = current.filterNot { it.id == id }
        // Decide which bucket it was in before
        val wasOverdue = (dose.minutesOverdue ?: 0) > 0

        // Use incrementStatus with positive/negative deltas
        if (wasOverdue) {
            incrementStatus(com.example.medicationadherenceapp.MedStatus.OVERDUE, -1)
        } else {
            incrementStatus(com.example.medicationadherenceapp.MedStatus.DUE, -1)
        }
        incrementStatus(com.example.medicationadherenceapp.MedStatus.TAKEN, 1)
    }

    fun skipDose(id: String) {
        // record this somewhere later
        // for now we just update overdue/due counts based on current value
        val current = _todayDoses.value
        val dose = current.firstOrNull { it.id == id } ?: return

        val isOverdue = (dose.minutesOverdue ?: 0) > 0
        if (isOverdue) {
            incrementStatus(com.example.medicationadherenceapp.MedStatus.OVERDUE, 1)
        } else {
            incrementStatus(com.example.medicationadherenceapp.MedStatus.DUE, 1)
        }
    }


}
