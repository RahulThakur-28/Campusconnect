package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.domain.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid


    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
        loadEvents()
    }

    private fun setUserRole(role: UserRole) {
        _uiState.update {
            it.copy(userRole = role)
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    setUserRole(user.role)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
        }
    }

    fun loadEvents() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            eventRepository.getAllEvents()
                .onSuccess { events ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            events = events,
                            featuredEvent = events.firstOrNull { event ->
                                event.isFeatured
                            }
                        )
                    }

                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }

                }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    fun onRegisterEvent(eventId: String) {

        val userId = currentUserId ?: return

        viewModelScope.launch {

            eventRepository.registerForEvent(eventId, userId)
                .onSuccess {

                    _uiState.update { state ->
                        state.copy(
                            registeredEventIds =
                                state.registeredEventIds + eventId
                        )
                    }

                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(error = exception.message)
                    }

                }

        }
    }

    fun onUnregisterEvent(eventId: String) {

        val userId = currentUserId ?: return

        viewModelScope.launch {

            eventRepository.unregisterFromEvent(eventId, userId)
                .onSuccess {

                    _uiState.update { state ->
                        state.copy(
                            registeredEventIds =
                                state.registeredEventIds - eventId
                        )
                    }

                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(error = exception.message)
                    }

                }

        }
    }

    fun refresh() {
        loadEvents()
    }
}