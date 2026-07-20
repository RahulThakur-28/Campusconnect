package com.rahul.campusconnect.presentation.home

import androidx.compose.runtime.Immutable
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.LostFoundItem

@Immutable
data class HomeUiState(
    val userName: String = "Rahul",
    val department: String = "CSE",
    val academicYear: String = "3rd Year",
    val isVerified: Boolean = true,
    val notificationCount: Int = 3,
    val announcements: List<Announcement> = emptyList(),
    val events: List<Event> = emptyList(),
    val placements: List<Placement> = emptyList(),
    val notes: List<Note> = emptyList(),
    val lostFoundItems: List<LostFoundItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


