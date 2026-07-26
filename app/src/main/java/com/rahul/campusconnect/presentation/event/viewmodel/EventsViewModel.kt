package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.event.state.EventTab
import com.rahul.campusconnect.presentation.event.state.EventsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    private var allEvents: List<Event> = emptyList()

    init {
        loadCurrentUser()
        loadEvents()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(userRole = user.role) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(error = exception.message) }
                }
        }
    }

    fun loadEvents(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) _uiState.update { it.copy(isRefreshing = true) }
            else _uiState.update { it.copy(isLoading = true, error = null) }

            eventRepository.getAllEvents()
                .onSuccess { events ->
                    allEvents = events
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isRefreshing = false, 
                            events = events,
                            featuredEvent = events.firstOrNull { e -> e.isFeatured }
                        ) 
                    }
                    applyFilters()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onTabSelected(tab: EventTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val now = System.currentTimeMillis()
        
        var filtered = allEvents

        // Filter by Tab
        filtered = when (state.selectedTab) {
            EventTab.UPCOMING -> filtered.filter { it.startDate > now }
            EventTab.ONGOING -> filtered.filter { it.startDate <= now && it.endDate >= now }
            EventTab.PAST -> filtered.filter { it.endDate < now }
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
                it.venue.contains(state.searchQuery, ignoreCase = true)
            }
        }

        _uiState.update { it.copy(filteredEvents = filtered) }
    }

    fun refresh() {
        loadEvents(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
