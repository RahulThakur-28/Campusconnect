package com.rahul.campusconnect.presentation.placement.state

import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.UserRole

data class PlacementDetailsUiState(

    val placement: Placement? = null,

    val isLoading: Boolean = false,

    val error: String? = null,

    val userRole: UserRole = UserRole.STUDENT,

    val isRefreshing: Boolean = false
) {

    val canEdit: Boolean
        get() = userRole == UserRole.ADMIN ||
                userRole == UserRole.PLACEMENT_CELL

    val canApply: Boolean
        get() = placement != null &&
                !placement.isDeleted &&
                placement.status.equals("Active", true)

    val isExpired: Boolean
        get() = placement?.deadline?.let {
            it < System.currentTimeMillis()
        } ?: false
}
