package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.Event
import com.rahul.campusconnect.model.UserRole
import com.rahul.campusconnect.presentation.event.state.EventsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
        // For demonstration, setting a default role. 
        // In a real app, this would come from an AuthRepository.
        _uiState.update { it.copy(userRole = UserRole.ADMIN) } 
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulating API call/Firestore fetch
            val dummyEvents = listOf(
                Event(
                    id = "1",
                    title = "TechQuest 2024",
                    description = "Annual coding competition for all engineering students. Showcase your skills and win exciting prizes.",
                    category = "Academic",
                    venue = "Auditorium A",
                    date = "Oct 24, 2024",
                    time = "10:00 AM",
                    registeredCount = 245,
                    isFeatured = true
                ),
                Event(
                    id = "2",
                    title = "AI Workshop",
                    description = "Hands-on workshop on Generative AI and Large Language Models. Learn from industry experts.",
                    category = "Workshop",
                    venue = "Lab 104",
                    date = "Nov 02, 2024",
                    time = "11:30 AM",
                    registeredCount = 180
                ),
                Event(
                    id = "3",
                    title = "Cultural Fest 2024",
                    description = "A grand celebration of music, dance, and art. Join us for a day of fun and creativity.",
                    category = "Cultural",
                    venue = "Main Ground",
                    date = "Dec 12, 2024",
                    time = "05:00 PM",
                    registeredCount = 520
                )
            )

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    events = dummyEvents,
                    featuredEvent = dummyEvents.find { e -> e.isFeatured }
                ) 
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onRegisterEvent(eventId: String) {
        _uiState.update { state ->
            val isRegistered = state.registeredEventIds.contains(eventId)
            val newRegisteredIds = if (isRegistered) {
                state.registeredEventIds - eventId
            } else {
                state.registeredEventIds + eventId
            }
            state.copy(registeredEventIds = newRegisteredIds)
        }
    }
}
