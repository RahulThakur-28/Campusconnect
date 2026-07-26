package com.rahul.campusconnect.presentation.notes.state

data class CreateNoteUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
