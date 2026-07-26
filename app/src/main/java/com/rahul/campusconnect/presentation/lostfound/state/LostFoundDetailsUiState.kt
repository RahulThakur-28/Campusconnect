package com.rahul.campusconnect.presentation.lostfound.state

import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.model.UserRole

data class LostFoundDetailsUiState(
    val item: LostFoundItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userRole: UserRole = UserRole.STUDENT,
    val currentUserId: String = "",
    val isDeleted: Boolean = false,
    val isDeleting: Boolean = false,
    val isResolving: Boolean = false
) {
    val canEditOrResolve: Boolean
        get() = userRole == UserRole.ADMIN || item?.ownerId == currentUserId
}
