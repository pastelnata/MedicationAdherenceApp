package com.example.medicationadherenceapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "user_prefs"

// Create a single DataStore instance attached to Context
internal val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

private val USER_ID_KEY = stringPreferencesKey("user_id")
private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

class DataStoreManager(private val context: Context) {

    /** Flow of [UserPreferences] that emits current preferences and updates. */
    val userPreferencesFlow: Flow<UserPreferences> = context.userPrefsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserPreferences(
                userId = prefs[USER_ID_KEY],
                isDarkMode = prefs[DARK_MODE_KEY] ?: false,
                notificationsEnabled = prefs[NOTIFICATIONS_KEY] ?: true
            )
        }

    /** Update or remove the stored user ID. Passing null removes the key. */
    suspend fun updateUserId(newId: String?) {
        context.userPrefsDataStore.edit { prefs ->
            if (newId == null) {
                prefs.remove(USER_ID_KEY)
            } else {
                prefs[USER_ID_KEY] = newId
            }
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun clear() {
        context.userPrefsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
