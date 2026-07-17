package com.rahul.campusconnect.presentation.more.state

data class MoreUiState(
    val userName: String = "Rahul Thakur",
    val userRole: String = "Verified Student",
    val department: String = "Computer Science",
    val year: String = "3rd Year",
    val profilePictureUrl: String? = null,
    val isVerified: Boolean = true,
    val role : String = "Student",
    val academicYear : String ="25-26"
)
