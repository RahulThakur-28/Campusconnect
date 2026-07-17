package com.rahul.campusconnect.presentation.placement.state

import com.rahul.campusconnect.model.Placement
import com.rahul.campusconnect.model.UserRole
import kotlin.coroutines.EmptyCoroutineContext.get

data class PlacementsUiState(
    val placements: List<Placement> = emptyList(),
    val featuredPlacement: Placement? = null,
    val categories: List<String> = listOf("All", "IT", "Finance", "Core", "Startup"),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val userRole: UserRole = UserRole.STUDENT,
    val error: String? = null,
    val activeDrives: Int = 0,
    val season: String = "2025-26") {

    val canCreatePlacement: Boolean
        get() = userRole == UserRole.ADMIN ||
                userRole == UserRole.PLACEMENT_CELL


}
