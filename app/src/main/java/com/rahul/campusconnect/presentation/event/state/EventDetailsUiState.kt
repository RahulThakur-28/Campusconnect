package com.rahul.campusconnect.presentation.event.state


import com.rahul.campusconnect.domain.model.Event

data class EventDetailsUiState(

    val event: Event? = null,

    val isLoading: Boolean = false,

    val isRegistered: Boolean = false,

    val error: String? = null

)