package com.rahul.campusconnect.presentation.placement.state

import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.UserRole

data class PlacementsUiState(
    val placements: List<Placement> = emptyList(),
    val featuredPlacement: Placement? = null,
    val categories: List<String> = listOf("All", "IT", "Finance", "Core", "Startup"),
    val jobTypes: List<String> = listOf("All", "Full-time", "Internship"),
    val locations: List<String> = listOf("All", "On Campus", "Off Campus", "Remote"),
    val sortOptions: List<String> = listOf("Newest", "Oldest", "Highest Package", "Deadline"),
    
    val selectedCategory: String = "All",
    val selectedJobType: String = "All",
    val selectedLocation: String = "All",
    val selectedSort: String = "Newest",
    
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val userRole: UserRole = UserRole.STUDENT,
    val error: String? = null,
    val activeDrives: Int = 0,
    val season: String = "2025-26"
) {
    val isEmpty: Boolean get() = !isLoading && placements.isEmpty()
    val canCreatePlacement: Boolean get() = userRole == UserRole.ADMIN || userRole == UserRole.PLACEMENT_CELL
}
