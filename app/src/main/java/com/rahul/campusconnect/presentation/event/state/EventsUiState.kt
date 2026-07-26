package com.rahul.campusconnect.presentation.event.state

import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.UserRole

data class EventsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val userRole: UserRole = UserRole.STUDENT,
    val events: List<Event> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val registeredEventIds: Set<String> = emptySet(),
    val featuredEvent: Event? = null,

    val categories: List<String> = listOf(
        "All",
        "Academic",
        "Workshop",
        "Hackathon",
        "Seminar",
        "Sports",
        "Cultural",
        "Placement",
        "Competition",
        "Technical",
        "Fest"
    ),

    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedTab: EventTab = EventTab.UPCOMING,
    val error: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && filteredEvents.isEmpty()
    
    val canCreateEvent: Boolean
        get() = userRole == UserRole.ADMIN ||
                userRole == UserRole.VERIFIED_TEACHER ||
                userRole == UserRole.PLACEMENT_CELL
}

enum class EventTab {
    UPCOMING, ONGOING, PAST
}
