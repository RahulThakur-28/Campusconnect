package com.rahul.campusconnect.presentation.home

import androidx.compose.runtime.Immutable
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.LostFoundItem

@Immutable
data class HomeUiState(
    val userName: String = "",
    val department: String = "",
    val academicYear: String = "",
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val role: String = "",
    val notificationCount: Int = 0,
    val announcements: List<Announcement> = emptyList(),
    val announcementsCount: Int = 0,
    val events: List<Event> = emptyList(),
    val eventsCount: Int = 0,
    val placements: List<Placement> = emptyList(),
    val placementsCount: Int = 0,
    val notes: List<Note> = emptyList(),
    val notesCount: Int = 0,
    val lostFoundItems: List<LostFoundItem> = emptyList(),
    val lostFoundItemsCount: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
