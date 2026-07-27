package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(collegeId: String, userId: String): Flow<List<Notification>>
    fun getUnreadCount(collegeId: String, userId: String): Flow<Int>
    suspend fun markAsRead(collegeId: String, notificationId: String): Result<Unit>
    suspend fun markAllAsRead(collegeId: String, userId: String): Result<Unit>
    suspend fun deleteNotification(collegeId: String, notificationId: String): Result<Unit>
    suspend fun sendNotification(notification: Notification): Result<Unit>
}
