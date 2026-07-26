package com.rahul.campusconnect.presentation.event.state

import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.UserRole

data class EventDetailsUiState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val isDeleted: Boolean = false,
    val userRole: UserRole = UserRole.STUDENT,
    val currentUserId: String = "",
    val error: String? = null
) {
    val canEdit: Boolean
        get() = userRole == UserRole.ADMIN || event?.organizerId == currentUserId
}
