package com.rahul.campusconnect.presentation.event.state

data class CreateEventUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)