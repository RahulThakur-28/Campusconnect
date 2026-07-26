package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.NotificationRemoteDataSource
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource,
    private val sessionManager: SessionManager
) : NotificationRepository {

    private fun getCollegeId(): String? = sessionManager.getCollegeId()

    override fun getNotifications(): Flow<List<Notification>> {
        val collegeId = getCollegeId() ?: return emptyFlow()
        return remoteDataSource.getNotifications(collegeId)
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.markAsRead(collegeId, notificationId)
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.markAllAsRead(collegeId)
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.deleteNotification(collegeId, notificationId)
    }
}
