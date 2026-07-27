package com.rahul.campusconnect.presentation.settings.state

import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.NotificationPreferences

data class SettingsUiState(
    val user: User? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1",
    val deviceInfo: String = "",
    val loginTime: String = "",
    val authProvider: String = ""
)
