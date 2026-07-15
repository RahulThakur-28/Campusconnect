package com.rahul.campusconnect.presentation.profile.state

import com.rahul.campusconnect.model.*

data class ProfileUiState(
    val user: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Activity related
    val myNotes: List<Note> = emptyList(),
    val myEvents: List<Event> = emptyList(),
    val myPlacements: List<Placement> = emptyList(),
    val myLostFoundItems: List<LostFoundItem> = emptyList()
)
