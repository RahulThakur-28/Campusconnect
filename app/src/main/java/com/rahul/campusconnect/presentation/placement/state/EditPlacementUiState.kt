package com.rahul.campusconnect.presentation.placement.state

import com.rahul.campusconnect.domain.model.Placement

data class EditPlacementUiState(

    val isLoading: Boolean = true,

    val isSubmitting: Boolean = false,

    val isSuccess: Boolean = false,

    val placement: Placement? = null,

    val error: String? = null
)