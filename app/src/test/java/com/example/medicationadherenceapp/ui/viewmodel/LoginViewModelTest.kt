package com.example.medicationadherenceapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for LoginViewModel.
 * Tests the login flow, validation, state management, and event emission.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // This rule makes LiveData/StateFlow updates synchronous for testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty email and password`() = runTest {
        // Given: ViewModel is initialized

        // Then: Email and password should be empty
        assertEquals("", viewModel.email.value)
        assertEquals("", viewModel.password.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `setEmail should update email state`() = runTest {
        // Given: ViewModel is initialized
        val testEmail = "test@example.com"

        // When: Set email
        viewModel.setEmail(testEmail)

        // Then: Email state should be updated
        assertEquals(testEmail, viewModel.email.value)
    }

    @Test
    fun `setPassword should update password state`() = runTest {
        // Given: ViewModel is initialized
        val testPassword = "password123"

        // When: Set password
        viewModel.setPassword(testPassword)

        // Then: Password state should be updated
        assertEquals(testPassword, viewModel.password.value)
    }

    @Test
    fun `login with empty email should show error`() = runTest {
        // Given: Empty email and valid password
        viewModel.setEmail("")
        viewModel.setPassword("password123")

        // When: Attempt to login
        viewModel.login(UserType.PATIENT)
        advanceUntilIdle()

        // Then: Should show error and not be loading
        assertFalse(viewModel.isLoading.value)
        assertEquals(
            "Please enter a valid email and password (min 4 chars).",
            viewModel.error.value
        )
    }

    @Test
    fun `login with invalid email should show error`() = runTest {
        // Given: Invalid email (no @ symbol) and valid password
        viewModel.setEmail("invalidemail")
        viewModel.setPassword("password123")

        // When: Attempt to login
        viewModel.login(UserType.PATIENT)
        advanceUntilIdle()

        // Then: Should show error
        assertFalse(viewModel.isLoading.value)
        assertEquals(
            "Please enter a valid email and password (min 4 chars).",
            viewModel.error.value
        )
    }

    @Test
    fun `login with short password should show error`() = runTest {
        // Given: Valid email but password too short (less than 4 chars)
        viewModel.setEmail("test@example.com")
        viewModel.setPassword("abc")

        // When: Attempt to login
        viewModel.login(UserType.PATIENT)
        advanceUntilIdle()

        // Then: Should show error
        assertFalse(viewModel.isLoading.value)
        assertEquals(
            "Please enter a valid email and password (min 4 chars).",
            viewModel.error.value
        )
    }

    @Test
    fun `login with valid credentials should emit success event`() = runTest {
        // Given: Valid email and password
        viewModel.setEmail("test@example.com")
        viewModel.setPassword("password123")

        // When: Login is successful
        viewModel.loginSuccess.test {
            viewModel.login(UserType.PATIENT)
            advanceUntilIdle()

            // Then: Should emit login success event
            val event = awaitItem()
            assertEquals(UserType.PATIENT, event)

            // And: Should not be loading and no error
            assertFalse(viewModel.isLoading.value)
            assertNull(viewModel.error.value)
        }
    }

    @Test
    fun `login should show loading state during process`() = runTest {
        // Given: Valid credentials
        viewModel.setEmail("test@example.com")
        viewModel.setPassword("password123")

        // When: Login is initiated
        viewModel.login(UserType.PATIENT)

        // Then: Should be in loading state initially
        assertTrue(viewModel.isLoading.value)

        // When: Login completes
        advanceUntilIdle()

        // Then: Should not be loading anymore
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `login with family user type should emit correct type`() = runTest {
        // Given: Valid credentials and FAMILY user type
        viewModel.setEmail("family@example.com")
        viewModel.setPassword("password123")

        // When: Login with FAMILY type
        viewModel.loginSuccess.test {
            viewModel.login(UserType.FAMILY)
            advanceUntilIdle()

            // Then: Should emit FAMILY user type
            val event = awaitItem()
            assertEquals(UserType.FAMILY, event)
        }
    }

    @Test
    fun `multiple login attempts while loading should be ignored`() = runTest {
        // Given: Valid credentials
        viewModel.setEmail("test@example.com")
        viewModel.setPassword("password123")

        // When: First login is initiated
        viewModel.login(UserType.PATIENT)
        assertTrue(viewModel.isLoading.value)

        // And: Second login attempt while still loading
        viewModel.login(UserType.PATIENT)

        // Then: Should still be loading (second call ignored)
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()
    }

    @Test
    fun `error should be cleared on new login attempt`() = runTest {
        // Given: Previous login failed with error
        viewModel.setEmail("")
        viewModel.setPassword("pass")
        viewModel.login(UserType.PATIENT)
        advanceUntilIdle()

        // Verify error is set
        assertEquals(
            "Please enter a valid email and password (min 4 chars).",
            viewModel.error.value
        )

        // When: Try to login again with valid credentials
        viewModel.setEmail("test@example.com")
        viewModel.setPassword("password123")
        viewModel.login(UserType.PATIENT)

        // Then: Error should be cleared
        assertNull(viewModel.error.value)
        advanceUntilIdle()
    }
}

