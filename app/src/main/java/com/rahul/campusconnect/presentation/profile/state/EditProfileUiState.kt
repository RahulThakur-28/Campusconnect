package com.rahul.campusconnect.presentation.profile.state


data class EditProfileUiState(

    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = "",

    val branch: String = "",
    val year: String = "",
    val section: String = "",

    val profileImage: String = "",

    val isSaving: Boolean = false,
    val error: String? = null,

    val isSuccess: Boolean = false

)