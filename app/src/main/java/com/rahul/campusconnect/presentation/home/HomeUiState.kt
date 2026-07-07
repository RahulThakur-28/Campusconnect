package com.rahul.campusconnect.presentation.home

import androidx.compose.runtime.Immutable

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
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val timestamp: String
)

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val venue: String,
    val imageUrl: String? = null,
    val status: String // e.g., "Registration Open", "Happening Now"
)

data class Placement(
    val id: String,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val role: String,
    val packageAmount: String,
    val deadline: String,
    val status: String // e.g., "Applied", "Active"
)
