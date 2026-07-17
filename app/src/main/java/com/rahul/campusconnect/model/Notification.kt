package com.rahul.campusconnect.model

enum class NotificationType {
    ANNOUNCEMENT,
    EVENT,
    PLACEMENT,
    NOTE,
    LOST_FOUND,
    GENERAL
}

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.GENERAL,
    val referenceId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
