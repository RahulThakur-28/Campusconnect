package com.rahul.campusconnect.presentation.auth.register

import android.net.Uri

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val collegeId: String = "",
    val enrollmentNumber: String = "",
    val department: String = "",
    val academicYear: String = "",
    val section: String? = null,
    val profileImage: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
