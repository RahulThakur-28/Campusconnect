package com.rahul.campusconnect.presentation.auth.register


import android.net.Uri

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val collegeId: String = "",
    val branch: String = "",

    val password: String = "",
    val confirmPassword: String = "",

    val profileImage: Uri? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)