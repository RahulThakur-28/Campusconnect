package com.rahul.campusconnect.presentation.profile.state

import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.User
data class ProfileUiState(

    val user: User = User(),

    val isLoading: Boolean = false,
    val error: String? = null,

    val myNotes: List<Note> = emptyList(),
    val myEvents: List<Event> = emptyList(),
    val myPlacements: List<Placement> = emptyList(),
    val myLostFoundItems: List<LostFoundItem> = emptyList()

)