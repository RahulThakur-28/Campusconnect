package com.rahul.campusconnect.presentation.notes.state

import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.UserRole

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedSemester: String = "All",
    val selectedBranch: String = "All",
    val selectedFileType: String = "All",
    val selectedSort: String = "Newest First",
    val userRole: UserRole = UserRole.STUDENT,
    val error: String? = null,
    
    val semesters: List<String> = listOf("All", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"),
    val branches: List<String> = listOf("All", "CSE", "IT", "ECE", "EE", "ME", "CE"),
    val fileTypes: List<String> = listOf("All", "PDF", "DOC", "PPT", "ZIP"),
    val sortOptions: List<String> = listOf("Newest First", "Oldest First", "Most Downloaded", "Alphabetical")
) {
    val isEmpty: Boolean get() = !isLoading && filteredNotes.isEmpty()
}
