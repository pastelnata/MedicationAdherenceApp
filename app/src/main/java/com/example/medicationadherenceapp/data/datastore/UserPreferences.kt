package com.example.medicationadherenceapp.data.datastore

/**
 * Simple immutable data holder for user preferences stored in DataStore.
 * Add fields here as your app needs more preferences.
 */
data class UserPreferences(
    val userId: String? = null,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)

