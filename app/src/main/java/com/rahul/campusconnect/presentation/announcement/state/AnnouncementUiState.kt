package com.rahul.campusconnect.presentation.announcement.state

import com.rahul.campusconnect.model.Announcement
import com.rahul.campusconnect.model.UserRole

data class AnnouncementUiState(
    val announcements: List<Announcement> = emptyList(),
    val filteredAnnouncements: List<Announcement> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val userRole: UserRole = UserRole.STUDENT,
    val error: String? = null,
    val categories: List<String> = listOf("All", "Academic", "Exam", "Holiday", "General", "Placement", "Events")


)
