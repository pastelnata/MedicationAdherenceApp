package com.example.medicationadherenceapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
 * Unit tests for ProgressViewModel.
 * Tests the selection state management for progress tabs/buckets.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var medicationRepository: MedicationRepository

    private lateinit var viewModel: ProgressViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProgressViewModel(medicationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have selected index 1`() = runTest {
        // Given: ViewModel is initialized

        // Then: Default selected index should be 1
        assertEquals(1, viewModel.selectedIndex.value)
    }

    @Test
    fun `setSelectedIndex should update selected index`() = runTest {
        // Given: ViewModel is initialized with default index 1

        // When: Set selected index to 0
        viewModel.setSelectedIndex(0)

        // Then: Selected index should be updated
        assertEquals(0, viewModel.selectedIndex.value)
    }

    @Test
    fun `setSelectedIndex can set to different values`() = runTest {
        // Given: ViewModel is initialized

        // When: Set to index 2
        viewModel.setSelectedIndex(2)

        // Then: Index should be 2
        assertEquals(2, viewModel.selectedIndex.value)

        // When: Set to index 5
        viewModel.setSelectedIndex(5)

        // Then: Index should be 5
        assertEquals(5, viewModel.selectedIndex.value)
    }

    @Test
    fun `selectedIndex StateFlow should emit new values`() = runTest {
        // Given: ViewModel is initialized
        val initialIndex = viewModel.selectedIndex.value

        // When: Update selected index
        viewModel.setSelectedIndex(3)

        // Then: New value should be emitted
        val newIndex = viewModel.selectedIndex.value
        assertEquals(1, initialIndex)
        assertEquals(3, newIndex)
    }

    @Test
    fun `multiple updates should reflect latest value`() = runTest {
        // Given: ViewModel is initialized

        // When: Multiple updates in sequence
        viewModel.setSelectedIndex(0)
        viewModel.setSelectedIndex(1)
        viewModel.setSelectedIndex(2)
        viewModel.setSelectedIndex(3)

        // Then: Should have latest value
        assertEquals(3, viewModel.selectedIndex.value)
    }

    @Test
    fun `setting same index should still update StateFlow`() = runTest {
        // Given: ViewModel with index 1
        assertEquals(1, viewModel.selectedIndex.value)

        // When: Set to same index
        viewModel.setSelectedIndex(1)

        // Then: Value should still be 1
        assertEquals(1, viewModel.selectedIndex.value)
    }

    @Test
    fun `setSelectedIndex with negative value should work`() = runTest {
        // Given: ViewModel is initialized

        // When: Set to negative index (edge case, but should work)
        viewModel.setSelectedIndex(-1)

        // Then: Should accept the value
        assertEquals(-1, viewModel.selectedIndex.value)
    }

    @Test
    fun `setSelectedIndex back to default should work`() = runTest {
        // Given: ViewModel with changed index
        viewModel.setSelectedIndex(5)
        assertEquals(5, viewModel.selectedIndex.value)

        // When: Set back to default (1)
        viewModel.setSelectedIndex(1)

        // Then: Should be back to 1
        assertEquals(1, viewModel.selectedIndex.value)
    }
}

