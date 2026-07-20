package com.rahul.campusconnect.presentation.notification.state

import com.rahul.campusconnect.domain.model.Notification

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val error: String? = null
)

enum class NotificationFilter(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    ANNOUNCEMENTS("Announcements"),
    EVENTS("Events"),
    PLACEMENTS("Placements")
}
