package com.rahul.campusconnect.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid


    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {

        viewModelScope.launch {

            userRepository.getCurrentUser()
                .onSuccess { user ->


                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            error = exception.message
                        )
                    }

                }

        }

    }

    fun loadEvent(eventId: String) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            eventRepository.getEventById(eventId)
                .onSuccess { event ->

                    _uiState.update {
                        it.copy(
                            event = event,
                            isLoading = false
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

    fun registerForEvent() {

        val eventId = uiState.value.event?.id ?: return
        val userId = currentUserId ?: return

        viewModelScope.launch {

            eventRepository.registerForEvent(
                eventId = eventId,
                userId = userId
            )
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isRegistered = true
                        )
                    }

                    loadEvent(eventId)

                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            error = exception.message
                        )
                    }

                }

        }

    }

    fun unregisterFromEvent() {

        val eventId = uiState.value.event?.id ?: return
        val userId = currentUserId ?: return

        viewModelScope.launch {

            eventRepository.unregisterFromEvent(
                eventId = eventId,
                userId = userId
            )
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isRegistered = false
                        )
                    }

                    loadEvent(eventId)

                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            error = exception.message
                        )
                    }

                }

        }

    }

    fun deleteEvent(
        onSuccess: () -> Unit
    ) {

        val eventId = uiState.value.event?.id ?: return

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            eventRepository.deleteEvent(eventId)
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()

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

    fun refresh() {

        uiState.value.event?.id?.let {
            loadEvent(it)
        }

    }

    fun clearError() {

        _uiState.update {
            it.copy(
                error = null
            )
        }

    }

}