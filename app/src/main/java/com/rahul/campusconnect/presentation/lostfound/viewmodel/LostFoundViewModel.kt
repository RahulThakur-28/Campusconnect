package com.rahul.campusconnect.presentation.lostfound.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundTab
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostFoundViewModel @Inject constructor(
    private val lostFoundRepository: LostFoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LostFoundUiState())
    val uiState: StateFlow<LostFoundUiState> = _uiState.asStateFlow()

    private var allItems: List<LostFoundItem> = emptyList()

    init {
        loadItems()
    }

    fun loadItems(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) _uiState.update { it.copy(isRefreshing = true) }
            else _uiState.update { it.copy(isLoading = true, error = null) }

            lostFoundRepository.getItems()
                .onSuccess { items ->
                    allItems = items
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, items = items) }
                    applyFilters()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message ?: "Unable to load items."
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onTabSelected(tab: LostFoundTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun onSortSelected(sort: String) {
        _uiState.update { it.copy(selectedSort = sort) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = allItems

        // Filter by Tab (LOST, FOUND, RESOLVED)
        filtered = filtered.filter { item ->
            when (state.selectedTab) {
                LostFoundTab.LOST -> item.type == "LOST" && item.status == "ACTIVE"
                LostFoundTab.FOUND -> item.type == "FOUND" && item.status == "ACTIVE"
                LostFoundTab.RESOLVED -> item.status == "RESOLVED"
            }
        }

        // Filter by Category
        if (state.selectedCategory != "All") {
            filtered = filtered.filter { it.category == state.selectedCategory }
        }

        // Search
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(state.searchQuery, ignoreCase = true) ||
                it.description.contains(state.searchQuery, ignoreCase = true) ||
                it.location.contains(state.searchQuery, ignoreCase = true) ||
                it.category.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Sorting
        filtered = when (state.selectedSort) {
            "Oldest First" -> filtered.sortedBy { it.createdAt }
            else -> filtered.sortedByDescending { it.createdAt } // Newest First
        }

        _uiState.update { it.copy(filteredItems = filtered) }
    }

    fun refresh() {
        loadItems(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
