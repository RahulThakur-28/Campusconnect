package com.rahul.campusconnect.presentation.announcement.state

import com.rahul.campusconnect.domain.model.Announcement

data class EditAnnouncementUiState(
    val announcement: Announcement? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isSubmitting: Boolean = false
)
