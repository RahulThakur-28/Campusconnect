package com.rahul.campusconnect.presentation.lostfound.state

import com.rahul.campusconnect.domain.model.LostFoundItem

data class EditLostFoundUiState(
    val item: LostFoundItem? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
