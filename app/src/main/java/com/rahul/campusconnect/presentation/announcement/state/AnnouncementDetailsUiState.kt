package com.rahul.campusconnect.presentation.announcement.state

import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.model.UserRole

data class AnnouncementDetailsUiState(
    val announcement: Announcement? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userRole: UserRole = UserRole.STUDENT,
    val currentUserId: String = "",
    val isDeleted: Boolean = false,
    val isDeleting: Boolean = false
) {
    val canEdit: Boolean
        get() = userRole == UserRole.ADMIN || 
                (userRole == UserRole.VERIFIED_TEACHER && announcement?.postedBy == currentUserId)
}
