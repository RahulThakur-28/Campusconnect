package com.rahul.campusconnect.presentation.event.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.event.state.CreateEventUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun createEvent(
        title: String,
        description: String,
        category: String,
        date: String,
        time: String,
        venue: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
            }

            userRepository.getCurrentUser()
                .onSuccess { user ->
                    var imageUrl = ""

                    if (imageUri != null) {
                        eventRepository.uploadEventImage(imageUri)
                            .onSuccess { url ->
                                imageUrl = url
                            }
                            .onFailure { exception ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = exception.message ?: "Image upload failed"
                                    )
                                }
                                return@launch
                            }
                    }

                    val event = Event(
                        title = title,
                        description = description,
                        imageUrl = imageUrl,
                        organizerId = user.uid,
                        organizerName = user.fullName,
                        organizerRole = user.role,
                        venue = venue,
                        category = category,
                        date = date,
                        time = time,
                        createdAt = System.currentTimeMillis()
                    )

                    eventRepository.createEvent(event)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                            }
                        }
                        .onFailure { exception ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = exception.message ?: "Failed to create event"
                                )
                            }
                        }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to get user info"
                        )
                    }
                }
        }
    }

    fun clearSuccess() {
        _uiState.update {
            it.copy(isSuccess = false)
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}
