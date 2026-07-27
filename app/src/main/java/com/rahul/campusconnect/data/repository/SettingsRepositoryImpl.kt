package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.common.datastore.SettingsDataStore
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.domain.repository.NotificationPreferences
import com.rahul.campusconnect.domain.repository.NotificationType
import com.rahul.campusconnect.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val pathProvider: FirestorePathProvider,
    private val sessionManager: SessionManager,
    private val storageManager: StorageManager
) : SettingsRepository {

    override fun getTheme(): Flow<AppTheme> = settingsDataStore.themeFlow

    override suspend fun setTheme(theme: AppTheme) = settingsDataStore.setTheme(theme)

    override fun getNotificationPreferences(): Flow<NotificationPreferences> = 
        settingsDataStore.notificationPreferencesFlow

    override suspend fun updateNotificationPreference(type: NotificationType, enabled: Boolean) =
        settingsDataStore.updateNotificationPreference(type, enabled)

    override suspend fun uploadScreenshot(uri: android.net.Uri): Result<String> {
        val fileName = "bug_${UUID.randomUUID()}.jpg"
        return storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = "bug_reports/$fileName",
            imageUri = uri
        )
    }

    override suspend fun submitBugReport(
        title: String,
        description: String,
        screenshotUrl: String?
    ): Result<Unit> = try {
        val collegeId = sessionManager.getCollegeId() ?: throw Exception("College ID not found")
        val userId = sessionManager.getUid() ?: throw Exception("User not logged in")
        
        val report = mapOf(
            "title" to title,
            "description" to description,
            "screenshotUrl" to screenshotUrl,
            "userId" to userId,
            "submittedAt" to System.currentTimeMillis()
        )
        
        pathProvider.bugReports(collegeId).add(report).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
