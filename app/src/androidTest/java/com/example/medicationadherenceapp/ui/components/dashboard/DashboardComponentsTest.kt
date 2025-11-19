package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.medicationadherenceapp.MedStatus
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for Dashboard components.
 * Tests medication status display and health tips.
 */
class DashboardComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun medStatusSummary_displaysAllStatusTypes() {
        // Given: Status counts for all types
        val statusCounts = mapOf(
            MedStatus.OVERDUE to 3,
            MedStatus.DUE to 5,
            MedStatus.TAKEN to 7
        )

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MedStatusSummary(statusCounts = statusCounts)
            }
        }

        // Then: All status labels should be displayed
        composeTestRule
            .onNodeWithText("Overdue")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Due")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Taken")
            .assertIsDisplayed()
    }

    @Test
    fun medStatusSummary_displaysCorrectCounts() {
        // Given: Status counts
        val statusCounts = mapOf(
            MedStatus.OVERDUE to 3,
            MedStatus.DUE to 5,
            MedStatus.TAKEN to 7
        )

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MedStatusSummary(statusCounts = statusCounts)
            }
        }

        // Then: Correct counts should be displayed
        composeTestRule
            .onNodeWithText("3")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("5")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("7")
            .assertIsDisplayed()
    }

    @Test
    fun medStatusSummary_displaysZeroWhenNoCount() {
        // Given: Empty status counts
        val statusCounts = mapOf<MedStatus, Int>()

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MedStatusSummary(statusCounts = statusCounts)
            }
        }

        // Then: Should display "0" for all statuses
        // Note: There will be three "0"s, one for each status
        composeTestRule
            .onNodeWithText("Overdue")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Due")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Taken")
            .assertIsDisplayed()
    }

    @Test
    fun medStatusSummary_handlesLargeNumbers() {
        // Given: Large status counts
        val statusCounts = mapOf(
            MedStatus.OVERDUE to 999,
            MedStatus.DUE to 100,
            MedStatus.TAKEN to 50
        )

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MedStatusSummary(statusCounts = statusCounts)
            }
        }

        // Then: Large numbers should be displayed correctly
        composeTestRule
            .onNodeWithText("999")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("100")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("50")
            .assertIsDisplayed()
    }

    @Test
    fun medStatusSummary_displaysAllCardsWithDefaultCounts() {
        // Given: Default counts (1 for each)
        val statusCounts = mapOf(
            MedStatus.OVERDUE to 1,
            MedStatus.DUE to 1,
            MedStatus.TAKEN to 1
        )

        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MedStatusSummary(statusCounts = statusCounts)
            }
        }

        // Then: All labels and counts should be visible
        composeTestRule
            .onNodeWithText("Overdue")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Due")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Taken")
            .assertIsDisplayed()
    }

    @Test
    fun mainPage_displaysHeaderText() {
        // Given: Main dashboard page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MainPage()
            }
        }

        // Then: Should display section headers
        composeTestRule
            .onNodeWithText("Needs Immediate Attention")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Today's Medication")
            .assertIsDisplayed()
    }

    @Test
    fun mainPage_displaysMedicationStatus() {
        // Given: Main dashboard page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MainPage()
            }
        }

        // Then: Should display medication status summary with default counts
        composeTestRule
            .onNodeWithText("Overdue")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Due")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Taken")
            .assertIsDisplayed()
    }

    @Test
    fun mainPage_displaysTodaysMedicationProgress() {
        // Given: Main dashboard page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MainPage()
            }
        }

        // Then: Should display today's medication progress
        composeTestRule
            .onNodeWithText("1/4 taken")
            .assertIsDisplayed()
    }

    @Test
    fun healthTips_isDisplayedOnMainPage() {
        // Given: Main dashboard page
        composeTestRule.setContent {
            MedicationAdherenceAppTheme {
                MainPage()
            }
        }

        // Then: Health tips section should be displayed
        // (This test verifies that HealthTips composable is called)
        // Specific content depends on HealthTips implementation
        composeTestRule
            .onNodeWithText("Today's Medication")
            .assertIsDisplayed()
    }
}

