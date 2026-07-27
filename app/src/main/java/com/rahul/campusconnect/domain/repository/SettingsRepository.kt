package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)

    fun getNotificationPreferences(): Flow<NotificationPreferences>
    suspend fun updateNotificationPreference(type: NotificationType, enabled: Boolean)

    suspend fun uploadScreenshot(uri: android.net.Uri): Result<String>

    suspend fun submitBugReport(title: String, description: String, screenshotUrl: String?): Result<Unit>
}

data class NotificationPreferences(
    val announcements: Boolean = true,
    val events: Boolean = true,
    val placements: Boolean = true,
    val notes: Boolean = true,
    val lostFound: Boolean = true,
    val discussionReplies: Boolean = true
)

enum class NotificationType {
    ANNOUNCEMENTS,
    EVENTS,
    PLACEMENTS,
    NOTES,
    LOST_FOUND,
    DISCUSSION_REPLIES
}
