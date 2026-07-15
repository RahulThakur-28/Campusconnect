package com.rahul.campusconnect.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.rahul.campusconnect.presentation.settings.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun togglePushNotifications(enabled: Boolean) {
        _uiState.update { it.copy(pushNotificationsEnabled = enabled) }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        _uiState.update { it.copy(emailNotificationsEnabled = enabled) }
    }

    fun toggleEventNotifications(enabled: Boolean) {
        _uiState.update { it.copy(eventNotificationsEnabled = enabled) }
    }

    fun togglePlacementNotifications(enabled: Boolean) {
        _uiState.update { it.copy(placementNotificationsEnabled = enabled) }
    }

    fun toggleAnnouncementNotifications(enabled: Boolean) {
        _uiState.update { it.copy(announcementNotificationsEnabled = enabled) }
    }

    fun toggleNotesNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notesNotificationsEnabled = enabled) }
    }

    fun toggleLostFoundNotifications(enabled: Boolean) {
        _uiState.update { it.copy(lostFoundNotificationsEnabled = enabled) }
    }

    fun logout() {
        // Implement logout logic
    }

    fun deleteAccount() {
        // Implement delete account logic
    }
}
