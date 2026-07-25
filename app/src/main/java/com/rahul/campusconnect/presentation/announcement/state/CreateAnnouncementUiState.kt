package com.rahul.campusconnect.presentation.announcement.state

data class CreateAnnouncementUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
