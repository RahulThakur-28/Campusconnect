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
    val events: List<Event> = emptyList(),
    val placements: List<Placement> = emptyList(),
    val notes: List<Note> = emptyList(),
    val lostFoundItems: List<LostFoundItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    val greeting: String
        get() = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning 👋"
            in 12..15 -> "Good Afternoon ☀️"
            in 16..20 -> "Good Evening 🌆"
            else -> "Good Night 🌙"
        }
}
