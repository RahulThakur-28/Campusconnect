package com.rahul.campusconnect.presentation.more.state

data class MoreUiState(
    val userName: String = "",
    val role: String = "",
    val department: String = "",
    val academicYear: String = "",
    val profilePictureUrl: String? = null,
    val isVerified: Boolean = false
)
