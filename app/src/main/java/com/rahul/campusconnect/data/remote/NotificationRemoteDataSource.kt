package com.rahul.campusconnect.data.remote

import com.rahul.campusconnect.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRemoteDataSource {
    fun getNotifications(collegeId: String): Flow<List<Notification>>
    suspend fun markAsRead(collegeId: String, notificationId: String): Result<Unit>
    suspend fun markAllAsRead(collegeId: String): Result<Unit>
    suspend fun deleteNotification(collegeId: String, notificationId: String): Result<Unit>
}
