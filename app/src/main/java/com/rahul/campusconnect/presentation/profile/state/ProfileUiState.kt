package com.rahul.campusconnect.presentation.profile.state

import com.rahul.campusconnect.domain.model.*

data class ProfileUiState(
    val user: User = User(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    
    val notesCount: Int = 0,
    val eventsCount: Int = 0,
    val placementsCount: Int = 0,
    val announcementsCount: Int = 0,
    val discussionsCount: Int = 0,
    
    val myNotes: List<Note> = emptyList(),
    val myEvents: List<Event> = emptyList(),
    val myPlacements: List<Placement> = emptyList(),
    val myAnnouncements: List<Announcement> = emptyList(),
    val myQuestions: List<Discussion> = emptyList(),
    val myLostFoundItems: List<LostFoundItem> = emptyList(),
    
    val verificationRequest: VerificationRequest? = null,
    val isRefreshing: Boolean = false
)
