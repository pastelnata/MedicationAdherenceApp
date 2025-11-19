package com.example.medicationadherenceapp.ui.components.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for Login Page components.
 * Tests user interactions, navigation, and UI state.
 */
class LoginPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userTypeSelection_displaysWelcomeMessage() {
        // Given: User type selection screen
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                UserTypeSelection(onSelect = {})
            }
        }

        // Then: Welcome message should be displayed
        composeTestRule
            .onNodeWithText("Welcome to MedCare")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Your comprehensive medication management system")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Who are you?")
            .assertIsDisplayed()
    }

    @Test
    fun userTypeSelection_displaysPatientOption() {
        // Given: User type selection screen
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                UserTypeSelection(onSelect = {})
            }
        }

        // Then: Patient option should be displayed
        composeTestRule
            .onNodeWithText("I'm a Patient")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Manage my medications and health")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Patient")
            .assertIsDisplayed()
    }

    @Test
    fun userTypeSelection_displaysFamilyOption() {
        // Given: User type selection screen
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                UserTypeSelection(onSelect = {})
            }
        }

        // Then: Family member option should be displayed
        composeTestRule
            .onNodeWithText("I'm a Family Member")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Support my loved ones")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Family Member")
            .assertIsDisplayed()
    }

    @Test
    fun userTypeSelection_patientCardIsClickable() {
        // Given: User type selection screen
        var selectedType: UserType? = null

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                UserTypeSelection(onSelect = { selectedType = it })
            }
        }

        // When: Click patient card
        composeTestRule
            .onNodeWithText("I'm a Patient")
            .performClick()

        // Then: Patient type should be selected
        assert(selectedType == UserType.PATIENT)
    }

    @Test
    fun userTypeSelection_familyCardIsClickable() {
        // Given: User type selection screen
        var selectedType: UserType? = null

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                UserTypeSelection(onSelect = { selectedType = it })
            }
        }

        // When: Click family member card
        composeTestRule
            .onNodeWithText("I'm a Family Member")
            .performClick()

        // Then: Family type should be selected
        assert(selectedType == UserType.FAMILY)
    }

    @Test
    fun loginScreen_displaysLoginButton() {
        // Given: Login screen for patient
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginScreen(
                    userType = UserType.PATIENT,
                    onBack = {},
                    onLogin = {}
                )
            }
        }

        // Then: Login button should be displayed
        composeTestRule
            .onNodeWithText("Login")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysBackButton() {
        // Given: Login screen
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginScreen(
                    userType = UserType.PATIENT,
                    onBack = {},
                    onLogin = {}
                )
            }
        }

        // Then: Back button should be displayed
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_backButtonTriggersCallback() {
        // Given: Login screen with callback
        var backPressed = false

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginScreen(
                    userType = UserType.PATIENT,
                    onBack = { backPressed = true },
                    onLogin = {}
                )
            }
        }

        // When: Click back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then: Back callback should be triggered
        assert(backPressed)
    }

    @Test
    fun loginPage_startsWithUserTypeSelection() {
        // Given: Login page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginPage(onLogin = {})
            }
        }

        // Then: Should display user type selection
        composeTestRule
            .onNodeWithText("Welcome to MedCare")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Who are you?")
            .assertIsDisplayed()
    }

    @Test
    fun loginPage_navigatesToLoginScreenAfterSelection() {
        // Given: Login page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginPage(onLogin = {})
            }
        }

        // When: Select patient option
        composeTestRule
            .onNodeWithText("I'm a Patient")
            .performClick()

        // Then: Should navigate to login screen
        // User type selection should not be visible
        composeTestRule
            .onNodeWithText("Who are you?")
            .assertDoesNotExist()

        // Login button should be visible
        composeTestRule
            .onNodeWithText("Login")
            .assertIsDisplayed()
    }

    @Test
    fun loginPage_canNavigateBackToUserTypeSelection() {
        // Given: Login page on login screen
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                LoginPage(onLogin = {})
            }
        }

        // When: Select patient and then go back
        composeTestRule
            .onNodeWithText("I'm a Patient")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then: Should be back at user type selection
        composeTestRule
            .onNodeWithText("Who are you?")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Login")
            .assertDoesNotExist()
    }
}

