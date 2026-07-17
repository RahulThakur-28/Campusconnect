package com.rahul.campusconnect.presentation.search.state

import com.rahul.campusconnect.model.SearchResult

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = listOf("Android Workshop", "Google Placement", "Operating Systems Notes"),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SearchFilter(val label: String) {
    ALL("All"),
    ANNOUNCEMENTS("Announcements"),
    EVENTS("Events"),
    PLACEMENTS("Placements"),
    NOTES("Notes"),
    LOST_FOUND("Lost & Found")
}
