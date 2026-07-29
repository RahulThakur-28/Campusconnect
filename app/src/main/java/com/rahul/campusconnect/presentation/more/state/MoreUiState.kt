package com.rahul.campusconnect.presentation.more.state

import com.rahul.campusconnect.domain.model.UserRole

data class MoreUiState(
    val userName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val department: String = "",
    val academicYear: String = "",
    val collegeName: String = "",
    val profilePictureUrl: String? = null,
    val isVerified: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)
