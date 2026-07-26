package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.event.state.EventDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            userRepository.getCurrentUser().onSuccess { user ->
                _uiState.update { it.copy(userRole = user.role, currentUserId = user.uid) }
                
                eventRepository.isUserRegistered(eventId, user.uid).onSuccess { isRegistered ->
                    _uiState.update { it.copy(isRegistered = isRegistered) }
                }
            }

            eventRepository.getEventById(eventId)
                .onSuccess { event ->
                    if (event == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Event not found") }
                    } else {
                        _uiState.update { it.copy(event = event, isLoading = false) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun registerForEvent() {
        val eventId = uiState.value.event?.id ?: return
        val userId = uiState.value.currentUserId
        if (userId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eventRepository.registerForEvent(eventId, userId)
                .onSuccess {
                    _uiState.update { it.copy(isRegistered = true, isLoading = false) }
                    loadEvent(eventId) // Reload to update count
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun unregisterFromEvent() {
        val eventId = uiState.value.event?.id ?: return
        val userId = uiState.value.currentUserId
        if (userId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eventRepository.unregisterFromEvent(eventId, userId)
                .onSuccess {
                    _uiState.update { it.copy(isRegistered = false, isLoading = false) }
                    loadEvent(eventId) // Reload to update count
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun deleteEvent() {
        val eventId = uiState.value.event?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            eventRepository.deleteEvent(eventId).onSuccess {
                _uiState.update { it.copy(isLoading = false, isDeleted = true) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    fun refresh() {
        uiState.value.event?.id?.let {
            loadEvent(it)
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
