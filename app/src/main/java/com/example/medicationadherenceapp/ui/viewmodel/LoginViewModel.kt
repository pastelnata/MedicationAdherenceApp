package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.medicationadherenceapp.UserType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simple ViewModel to hoist login-related UI state out of composables.
 * Exposes StateFlows for email, password, loading and error.
 * Emits a one-shot loginSuccess event when login completes.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: com.example.medicationadherenceapp.repository.UserRepository) : ViewModel() {
    // Backing mutable state flows are kept private to enforce single-writer
    // semantics. UI observes the public read-only StateFlow properties.
    private val _email = MutableStateFlow("")
    // Public read-only view of the email text field.
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    // Public read-only view of the password text field.
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    // Indicates whether a login request is in progress. UI can show a
    // loading spinner when true.
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    // Holds an error message to display on the UI, or null when there's no error.
    val error: StateFlow<String?> = _error

    // One-shot events (like navigation after successful login) are modeled
    // with a SharedFlow so the UI can collect them and handle them once.
    private val _loginSuccess = MutableSharedFlow<UserType>()
    val loginSuccess: SharedFlow<UserType> = _loginSuccess

    // UI event handlers: update the backing StateFlows. Keep logic out of
    // composables and inside the ViewModel to follow MVVM and unidirectional
    // data flow principles.
    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    /**
     * Simulate a login flow. In a real app this would call into a repository.
     * The function performs basic validation and emits either an error or a
     * login success event. viewModelScope is used so work is canceled if
     * the ViewModel is cleared.
     */
    fun login(userType: UserType) {
        // prevent duplicate calls
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Simulate network delay
            delay(700)

            // Simple validation
            //val e = _email.value.trim()
            //val p = _password.value
            //if (e.isEmpty() || p.length < 4 || !e.contains("@")) {
            //    _error.value = "Please enter a valid email and password (min 4 chars)."
            //    _isLoading.value = false
            //    return@launch
            //}

            // Simulate success: clear loading and emit a one-shot event so the
            // UI can navigate away (for example). In production, you'd handle
            // repository errors and map them to user-friendly messages.
            _isLoading.value = false
            _error.value = null
            _loginSuccess.emit(userType)
        }
    }
}
