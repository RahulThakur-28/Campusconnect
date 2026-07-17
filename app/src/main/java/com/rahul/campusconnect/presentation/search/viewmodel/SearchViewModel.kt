package com.rahul.campusconnect.presentation.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.SearchResult
import com.rahul.campusconnect.model.SearchResultType
import com.rahul.campusconnect.presentation.search.state.SearchFilter
import com.rahul.campusconnect.presentation.search.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        if (newQuery.length >= 2) {
            performSearch()
        } else if (newQuery.isEmpty()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false) }
        }
    }

    fun onFilterSelected(filter: SearchFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        if (_uiState.value.query.isNotEmpty()) {
            performSearch()
        }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "", results = emptyList()) }
    }

    fun onRecentSearchSelected(search: String) {
        onQueryChanged(search)
    }

    fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    private fun performSearch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500) // Simulate network delay

            val allDummyResults = listOf(
                SearchResult("1", "Android Development Workshop", "20 July • Auditorium", SearchResultType.EVENT),
                SearchResult("2", "End Semester Examination Schedule", "Academic Section", SearchResultType.ANNOUNCEMENT),
                SearchResult("3", "Google SDE Internship", "Placement Cell • 45 LPA", SearchResultType.PLACEMENT),
                SearchResult("4", "Operating Systems Unit 4", "Computer Science • Prof. Sharma", SearchResultType.NOTE),
                SearchResult("5", "Black Backpack Found", "Library • Today", SearchResultType.LOST_FOUND),
                SearchResult("6", "AI & ML Seminar", "15 Aug • Conference Hall", SearchResultType.EVENT),
                SearchResult("7", "Holiday Notice: Diwali", "General", SearchResultType.ANNOUNCEMENT)
            )

            val query = _uiState.value.query.lowercase()
            val filter = _uiState.value.selectedFilter

            val filteredResults = allDummyResults.filter { result ->
                val matchesQuery = result.title.lowercase().contains(query) || 
                                 result.subtitle.lowercase().contains(query)
                
                val matchesFilter = when (filter) {
                    SearchFilter.ALL -> true
                    SearchFilter.ANNOUNCEMENTS -> result.type == SearchResultType.ANNOUNCEMENT
                    SearchFilter.EVENTS -> result.type == SearchResultType.EVENT
                    SearchFilter.PLACEMENTS -> result.type == SearchResultType.PLACEMENT
                    SearchFilter.NOTES -> result.type == SearchResultType.NOTE
                    SearchFilter.LOST_FOUND -> result.type == SearchResultType.LOST_FOUND
                }
                
                matchesQuery && matchesFilter
            }

            _uiState.update { it.copy(results = filteredResults, isLoading = false) }
        }
    }
}
