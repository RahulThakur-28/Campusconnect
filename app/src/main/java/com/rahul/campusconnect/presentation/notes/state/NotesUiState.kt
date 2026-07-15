package com.rahul.campusconnect.presentation.notes.state

import com.rahul.campusconnect.model.Note
import com.rahul.campusconnect.model.UserRole

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedSemester: String = "All",
    val selectedDepartment: String = "All",
    val userRole: UserRole = UserRole.STUDENT,
    val error: String? = null
)
