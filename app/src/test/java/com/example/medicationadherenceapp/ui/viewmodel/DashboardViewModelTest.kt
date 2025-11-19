package com.example.medicationadherenceapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medicationadherenceapp.MedStatus
import com.example.medicationadherenceapp.repository.HealthTipRepository
import com.example.medicationadherenceapp.repository.MedicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

/**
 * Unit tests for DashboardViewModel.
 * Tests medication status count management and state updates.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var medicationRepository: MedicationRepository

    @Mock
    private lateinit var healthTipRepository: HealthTipRepository

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(medicationRepository, healthTipRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default counts`() = runTest {
        // Given: ViewModel is initialized

        // Then: Should have default status counts
        val statusCounts = viewModel.statusCounts.value
        assertEquals(1, statusCounts[MedStatus.OVERDUE])
        assertEquals(1, statusCounts[MedStatus.DUE])
        assertEquals(1, statusCounts[MedStatus.TAKEN])
    }

    @Test
    fun `setStatusCount should update specific status count`() = runTest {
        // Given: ViewModel is initialized
        val newCount = 5

        // When: Set OVERDUE count to 5
        viewModel.setStatusCount(MedStatus.OVERDUE, newCount)

        // Then: OVERDUE count should be updated
        assertEquals(newCount, viewModel.statusCounts.value[MedStatus.OVERDUE])

        // And: Other counts should remain unchanged
        assertEquals(1, viewModel.statusCounts.value[MedStatus.DUE])
        assertEquals(1, viewModel.statusCounts.value[MedStatus.TAKEN])
    }

    @Test
    fun `setStatusCount should emit new state`() = runTest {
        // Given: ViewModel is initialized
        val initialCounts = viewModel.statusCounts.value

        // When: Set a new count
        viewModel.setStatusCount(MedStatus.DUE, 3)

        // Then: StateFlow should emit new value (new map instance)
        val newCounts = viewModel.statusCounts.value
        assert(initialCounts !== newCounts) // Different object reference
        assertEquals(3, newCounts[MedStatus.DUE])
    }

    @Test
    fun `incrementStatus should increase count by delta`() = runTest {
        // Given: ViewModel with initial counts
        viewModel.setStatusCount(MedStatus.OVERDUE, 2)

        // When: Increment OVERDUE by 3
        viewModel.incrementStatus(MedStatus.OVERDUE, 3)

        // Then: Count should be increased by delta
        assertEquals(5, viewModel.statusCounts.value[MedStatus.OVERDUE])
    }

    @Test
    fun `incrementStatus with default delta should increase by 1`() = runTest {
        // Given: ViewModel with initial counts
        viewModel.setStatusCount(MedStatus.TAKEN, 10)

        // When: Increment TAKEN without specifying delta (default = 1)
        viewModel.incrementStatus(MedStatus.TAKEN)

        // Then: Count should be increased by 1
        assertEquals(11, viewModel.statusCounts.value[MedStatus.TAKEN])
    }

    @Test
    fun `incrementStatus with negative delta should decrease count`() = runTest {
        // Given: ViewModel with initial counts
        viewModel.setStatusCount(MedStatus.DUE, 10)

        // When: Increment with negative delta
        viewModel.incrementStatus(MedStatus.DUE, -3)

        // Then: Count should be decreased
        assertEquals(7, viewModel.statusCounts.value[MedStatus.DUE])
    }

    @Test
    fun `incrementStatus on non-existent status should handle gracefully`() = runTest {
        // Given: ViewModel is initialized
        viewModel.resetCounts() // All counts are 0

        // When: Increment a status that might not be in the map
        viewModel.incrementStatus(MedStatus.OVERDUE, 5)

        // Then: Should set count to delta (0 + 5)
        assertEquals(5, viewModel.statusCounts.value[MedStatus.OVERDUE])
    }

    @Test
    fun `resetCounts should set all counts to zero`() = runTest {
        // Given: ViewModel with non-zero counts
        viewModel.setStatusCount(MedStatus.OVERDUE, 10)
        viewModel.setStatusCount(MedStatus.DUE, 5)
        viewModel.setStatusCount(MedStatus.TAKEN, 15)

        // When: Reset counts
        viewModel.resetCounts()

        // Then: All counts should be zero
        assertEquals(0, viewModel.statusCounts.value[MedStatus.OVERDUE])
        assertEquals(0, viewModel.statusCounts.value[MedStatus.DUE])
        assertEquals(0, viewModel.statusCounts.value[MedStatus.TAKEN])
    }

    @Test
    fun `multiple status updates should all be reflected`() = runTest {
        // Given: ViewModel is initialized

        // When: Multiple updates are made
        viewModel.setStatusCount(MedStatus.OVERDUE, 3)
        viewModel.setStatusCount(MedStatus.DUE, 7)
        viewModel.setStatusCount(MedStatus.TAKEN, 12)

        // Then: All counts should be updated correctly
        val counts = viewModel.statusCounts.value
        assertEquals(3, counts[MedStatus.OVERDUE])
        assertEquals(7, counts[MedStatus.DUE])
        assertEquals(12, counts[MedStatus.TAKEN])
    }

    @Test
    fun `statusCounts StateFlow should be observable`() = runTest {
        // Given: ViewModel is initialized

        // When: Observe statusCounts and update
        val initialValue = viewModel.statusCounts.value
        viewModel.setStatusCount(MedStatus.OVERDUE, 100)

        // Then: Value should be updated and observable
        val newValue = viewModel.statusCounts.value
        assert(initialValue !== newValue)
        assertEquals(100, newValue[MedStatus.OVERDUE])
    }
}

