package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MyEventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadMyEvents()
    }

    fun loadMyEvents(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) _uiState.update { it.copy(isRefreshing = true) }
            else _uiState.update { it.copy(isLoading = true, error = null) }
            
            userRepository.getCurrentUser().onSuccess { user ->
                eventRepository.getMyEvents(user.uid).onSuccess { events ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, events = events, filteredEvents = events) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadMyEvents(isRefreshing = true)
    }
}
