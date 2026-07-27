package com.rahul.campusconnect.domain.model

enum class NotificationType {
    ANNOUNCEMENT,
    EVENT,
    PLACEMENT,
    VERIFICATION_APPROVED,
    VERIFICATION_REJECTED,
    DISCUSSION_REPLY,
    LOST_FOUND,
    GENERAL
}

data class Notification(
    val id: String = "",
    val userId: String = "", // "ALL" for global, or specific userId
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.GENERAL,
    val relatedId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val collegeId: String = ""
)
