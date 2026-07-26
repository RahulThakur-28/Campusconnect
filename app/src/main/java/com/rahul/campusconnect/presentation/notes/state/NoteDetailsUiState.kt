package com.rahul.campusconnect.presentation.notes.state

import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.UserRole

data class NoteDetailsUiState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userRole: UserRole = UserRole.STUDENT,
    val currentUserId: String = "",
    val isDeleted: Boolean = false,
    val isDeleting: Boolean = false,
    val isDownloading: Boolean = false
) {
    val canEdit: Boolean
        get() = userRole == UserRole.ADMIN || (uploadedByCurrentUser)
        
    val uploadedByCurrentUser: Boolean
        get() = note?.uploadedBy == currentUserId
}
