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
    private fun getUserId(): String? = sessionManager.getUid()

    override fun getNotifications(collegeId: String, userId: String): Flow<List<Notification>> {
        return remoteDataSource.getNotifications(collegeId, userId)
    }

    override fun getUnreadCount(collegeId: String, userId: String): Flow<Int> {
        return remoteDataSource.getUnreadCount(collegeId, userId)
    }

    override suspend fun markAsRead(collegeId: String, notificationId: String): Result<Unit> {
        return remoteDataSource.markAsRead(collegeId, notificationId)
    }

    override suspend fun markAllAsRead(collegeId: String, userId: String): Result<Unit> {
        return remoteDataSource.markAllAsRead(collegeId, userId)
    }

    override suspend fun deleteNotification(collegeId: String, notificationId: String): Result<Unit> {
        return remoteDataSource.deleteNotification(collegeId, notificationId)
    }

    override suspend fun sendNotification(notification: Notification): Result<Unit> {
        return remoteDataSource.sendNotification(notification)
    }
}
