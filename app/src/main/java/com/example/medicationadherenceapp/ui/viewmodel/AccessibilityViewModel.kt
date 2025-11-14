package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel to hoist accessibility-related UI state.
 * The UI observes these state flows to enable/disable accessibility features
 * such as voice prompts, high contrast, and larger text elements.
 */
@HiltViewModel
class AccessibilityViewModel @Inject constructor() : ViewModel() {
    private val _sliderPosition = MutableStateFlow(0.33f)
    val sliderPosition: StateFlow<Float> = _sliderPosition

    private val _voicePromptsEnabled = MutableStateFlow(true)
    val voicePromptsEnabled: StateFlow<Boolean> = _voicePromptsEnabled

    private val _soundAlertsEnabled = MutableStateFlow(true)
    val soundAlertsEnabled: StateFlow<Boolean> = _soundAlertsEnabled

    private val _highContrastEnabled = MutableStateFlow(false)
    val highContrastEnabled: StateFlow<Boolean> = _highContrastEnabled

    private val _largeTextElementsEnabled = MutableStateFlow(false)
    val largeTextElementsEnabled: StateFlow<Boolean> = _largeTextElementsEnabled

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    // Setter methods keep the ViewModel as the single source of truth for
    // accessibility settings and enable easy testing.
    fun setSliderPosition(value: Float) {
        _sliderPosition.value = value
    }

    fun setVoicePromptsEnabled(value: Boolean) {
        _voicePromptsEnabled.value = value
    }

    fun setSoundAlertsEnabled(value: Boolean) {
        _soundAlertsEnabled.value = value
    }

    fun setHighContrastEnabled(value: Boolean) {
        _highContrastEnabled.value = value
    }

    fun setLargeTextElementsEnabled(value: Boolean) {
        _largeTextElementsEnabled.value = value
    }

    fun setSelectedLanguage(value: String) {
        _selectedLanguage.value = value
    }
}
