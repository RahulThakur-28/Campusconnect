package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.UserRole
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

    val uiState: StateFlow<EventsUiState> =
        _uiState.asStateFlow()


    init {

        // Temporary role for UI testing.
        // Later this will come from AuthRepository / UserRepository.
        setUserRole(UserRole.ADMIN)

        loadEvents()
    }


    /**
     * Updates current user role and event creation permission.
     */
    private fun setUserRole(role: UserRole) {

        val canCreateEvent = when (role) {

            UserRole.ADMIN,
            UserRole.VERIFIED_TEACHER -> true

            else -> false
        }

        _uiState.update { currentState ->

            currentState.copy(
                userRole = role
            )
        }
    }


    /**
     * Loads events.
     * Currently using dummy data.
     * Later replace with repository / Firestore.
     */
    private fun loadEvents() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }


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


            _uiState.update { currentState ->

                currentState.copy(
                    isLoading = false,
                    events = dummyEvents,
                    featuredEvent = dummyEvents.find { event ->
                        event.isFeatured
                    }
                )
            }
        }
    }


    /**
     * Changes selected event category.
     */
    fun onCategorySelected(category: String) {

        _uiState.update {
            it.copy(
                selectedCategory = category
            )
        }
    }


    /**
     * Updates event search query.
     */
    fun onSearchQueryChanged(query: String) {

        _uiState.update {
            it.copy(
                searchQuery = query
            )
        }
    }


    /**
     * Registers or unregisters the user from an event.
     */
    fun onRegisterEvent(eventId: String) {

        _uiState.update { state ->

            val isAlreadyRegistered =
                state.registeredEventIds.contains(eventId)


            val updatedRegisteredIds = if (isAlreadyRegistered) {

                state.registeredEventIds - eventId

            } else {

                state.registeredEventIds + eventId
            }


            state.copy(
                registeredEventIds = updatedRegisteredIds
            )
        }
    }
}