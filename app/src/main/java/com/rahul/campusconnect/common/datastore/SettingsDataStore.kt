package com.rahul.campusconnect.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.domain.repository.NotificationPreferences
import com.rahul.campusconnect.domain.repository.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("app_theme")
    
    private val notifyAnnouncementsKey = booleanPreferencesKey("notify_announcements")
    private val notifyEventsKey = booleanPreferencesKey("notify_events")
    private val notifyPlacementsKey = booleanPreferencesKey("notify_placements")
    private val notifyNotesKey = booleanPreferencesKey("notify_notes")
    private val notifyLostFoundKey = booleanPreferencesKey("notify_lost_found")
    private val notifyDiscussionRepliesKey = booleanPreferencesKey("notify_discussion_replies")

    val themeFlow: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[themeKey] ?: AppTheme.SYSTEM.name
            AppTheme.valueOf(themeName)
        }

    val notificationPreferencesFlow: Flow<NotificationPreferences> = context.dataStore.data
        .map { preferences ->
            NotificationPreferences(
                announcements = preferences[notifyAnnouncementsKey] ?: true,
                events = preferences[notifyEventsKey] ?: true,
                placements = preferences[notifyPlacementsKey] ?: true,
                notes = preferences[notifyNotesKey] ?: true,
                lostFound = preferences[notifyLostFoundKey] ?: true,
                discussionReplies = preferences[notifyDiscussionRepliesKey] ?: true
            )
        }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.name
        }
    }

    suspend fun updateNotificationPreference(type: NotificationType, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            val key = when (type) {
                NotificationType.ANNOUNCEMENTS -> notifyAnnouncementsKey
                NotificationType.EVENTS -> notifyEventsKey
                NotificationType.PLACEMENTS -> notifyPlacementsKey
                NotificationType.NOTES -> notifyNotesKey
                NotificationType.LOST_FOUND -> notifyLostFoundKey
                NotificationType.DISCUSSION_REPLIES -> notifyDiscussionRepliesKey
            }
            preferences[key] = enabled
        }
    }
}
