package com.rahul.campusconnect.presentation.lostfound.state

data class CreateLostFoundUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
