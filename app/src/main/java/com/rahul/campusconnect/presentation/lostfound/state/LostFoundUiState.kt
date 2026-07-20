package com.rahul.campusconnect.presentation.lostfound.state

import com.rahul.campusconnect.domain.model.LostFoundItem

data class LostFoundUiState(
    val items: List<LostFoundItem> = emptyList(),
    val filteredItems: List<LostFoundItem> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedTab: LostFoundTab = LostFoundTab.LOST,
    val error: String? = null
)

enum class LostFoundTab {
    LOST, FOUND
}
