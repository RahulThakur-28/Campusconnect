package com.rahul.campusconnect.presentation.event.state

import com.rahul.campusconnect.domain.model.Event

data class EditEventUiState(

    val event: Event? = null,

    val isLoading: Boolean = false,

    val isSuccess: Boolean = false,

    val error: String? = null

)