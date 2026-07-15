package com.rahul.campusconnect.presentation.settings.state

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val eventNotificationsEnabled: Boolean = true,
    val placementNotificationsEnabled: Boolean = true,
    val announcementNotificationsEnabled: Boolean = true,
    val notesNotificationsEnabled: Boolean = true,
    val lostFoundNotificationsEnabled: Boolean = true,
    val appVersion: String = "1.0.0",
    val campusConnectVersion: String = "v1.0-stable"
)
