package com.example.medicationadherenceapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.remote.RemoteDataSource
import com.example.medicationadherenceapp.test.TestDataFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Unit tests for MedicationRepository.
 * Tests repository operations with mocked DAOs and remote data source.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MedicationRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var medicationDao: MedicationDao

    @Mock
    private lateinit var intakeDao: MedicationIntakeDao

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    private lateinit var repository: MedicationRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = MedicationRepository(medicationDao, intakeDao, remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addMedication should insert medication into local database`() = runTest {
        // Given: A test medication
        val medication = TestDataFactory.createTestMedication()

        // When: Add medication
        repository.addMedication(medication)

        // Then: Should insert into DAO
        verify(medicationDao).insertMedication(medication)
    }

    @Test
    fun `scheduleMedication should insert schedule into local database`() = runTest {
        // Given: A test medication schedule
        val schedule = TestDataFactory.createTestMedicationSchedule()

        // When: Schedule medication
        repository.scheduleMedication(schedule)

        // Then: Should insert into DAO
        verify(medicationDao).insertMedicationSchedule(schedule)
    }

    @Test
    fun `getSchedules should return flow from DAO`() = runTest {
        // Given: Patient ID and test schedules
        val patientId = UUID.randomUUID()
        val testSchedules = TestDataFactory.createTestScheduleList(patientId = patientId)
        whenever(medicationDao.getMedicationSchedules(patientId))
            .thenReturn(flowOf(testSchedules))

        // When: Get schedules
        val result = repository.getSchedules(patientId)

        // Then: Should return flow from DAO
        assertNotNull(result)
        verify(medicationDao).getMedicationSchedules(patientId)
    }

    @Test
    fun `addIntake should insert intake record into local database`() = runTest {
        // Given: A test intake record
        val intakeRecord = TestDataFactory.createTestIntakeRecord()

        // When: Add intake record
        repository.addIntake(intakeRecord)

        // Then: Should insert into DAO
        verify(intakeDao).insertIntakeRecord(intakeRecord)
    }

    @Test
    fun `getMedication should call DAO with correct ID`() = runTest {
        // Given: Medication ID and test medication
        val medicationId = UUID.randomUUID()
        val testMedication = TestDataFactory.createTestMedication(medicationId = medicationId)
        whenever(medicationDao.getMedication(medicationId))
            .thenReturn(testMedication)

        // When: Get medication
        val result = repository.getMedication(medicationId)

        // Then: Should call DAO with correct ID
        verify(medicationDao).getMedication(medicationId)
        assertEquals(testMedication, result)
    }

    @Test
    fun `updateMedication should update medication in local database`() = runTest {
        // Given: A medication to update
        val medication = TestDataFactory.createTestMedication(name = "Updated Medication")

        // When: Update medication
        repository.updateMedication(medication)

        // Then: Should call update on DAO
        verify(medicationDao).updateMedication(medication)
    }

    @Test
    fun `deleteMedication should delete medication from local database`() = runTest {
        // Given: Medication ID to delete
        val medicationId = UUID.randomUUID()

        // When: Delete medication
        repository.deleteMedication(medicationId)

        // Then: Should call delete on DAO
        verify(medicationDao).deleteMedication(medicationId)
    }

    @Test
    fun `getSchedule should call DAO with correct ID`() = runTest {
        // Given: Schedule ID and test schedule
        val scheduleId = UUID.randomUUID()
        val testSchedule = TestDataFactory.createTestMedicationSchedule(scheduleId = scheduleId)
        whenever(medicationDao.getSchedule(scheduleId))
            .thenReturn(testSchedule)

        // When: Get schedule
        val result = repository.getSchedule(scheduleId)

        // Then: Should call DAO with correct ID
        verify(medicationDao).getSchedule(scheduleId)
        assertEquals(testSchedule, result)
    }

    @Test
    fun `updateSchedule should update schedule in local database`() = runTest {
        // Given: A schedule to update
        val schedule = TestDataFactory.createTestMedicationSchedule(scheduledTime = "10:00")

        // When: Update schedule
        repository.updateSchedule(schedule)

        // Then: Should call update on DAO
        verify(medicationDao).updateMedicationSchedule(schedule)
    }

    @Test
    fun `deleteSchedule should delete schedule from local database`() = runTest {
        // Given: Schedule ID to delete
        val scheduleId = UUID.randomUUID()

        // When: Delete schedule
        repository.deleteSchedule(scheduleId)

        // Then: Should call delete on DAO
        verify(medicationDao).deleteMedicationSchedule(scheduleId)
    }

    @Test
    fun `updateIntake should update intake record in local database`() = runTest {
        // Given: An intake record to update
        val intakeRecord = TestDataFactory.createTestIntakeRecord(taken = false)

        // When: Update intake
        repository.updateIntake(intakeRecord)

        // Then: Should call update on DAO
        verify(intakeDao).updateIntakeRecord(intakeRecord)
    }

    @Test
    fun `deleteIntake should delete intake record from local database`() = runTest {
        // Given: Intake ID to delete
        val intakeId = UUID.randomUUID()

        // When: Delete intake
        repository.deleteIntake(intakeId)

        // Then: Should call delete on DAO
        verify(intakeDao).deleteIntake(intakeId)
    }

    @Test
    fun `multiple operations should all interact with DAO correctly`() = runTest {
        // Given: Multiple test entities
        val medication = TestDataFactory.createTestMedication()
        val schedule = TestDataFactory.createTestMedicationSchedule()
        val intake = TestDataFactory.createTestIntakeRecord()

        // When: Perform multiple operations
        repository.addMedication(medication)
        repository.scheduleMedication(schedule)
        repository.addIntake(intake)

        // Then: All operations should be called on respective DAOs
        verify(medicationDao).insertMedication(medication)
        verify(medicationDao).insertMedicationSchedule(schedule)
        verify(intakeDao).insertIntakeRecord(intake)
    }
}

