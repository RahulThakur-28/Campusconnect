package com.rahul.campusconnect.presentation.notes.state

import com.rahul.campusconnect.domain.model.Note

data class EditNoteUiState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
