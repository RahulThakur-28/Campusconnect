package com.rahul.campusconnect.presentation.lostfound.state

import com.rahul.campusconnect.domain.model.LostFoundItem

data class LostFoundUiState(
    val items: List<LostFoundItem> = emptyList(),
    val filteredItems: List<LostFoundItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedTab: LostFoundTab = LostFoundTab.LOST,
    val selectedCategory: String = "All",
    val selectedSort: String = "Newest First",
    val error: String? = null,
    val categories: List<String> = listOf("All", "Electronics", "Documents", "Accessories", "Books", "Personal Items", "Others"),
    val sortOptions: List<String> = listOf("Newest First", "Oldest First")
) {
    val isEmpty: Boolean get() = !isLoading && filteredItems.isEmpty()
}

enum class LostFoundTab {
    LOST, FOUND, RESOLVED
}
