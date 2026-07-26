package com.rahul.campusconnect.presentation.event.viewmodel

import android.net.Uri
import android.util.Log
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
        startDate: Long,
        endDate: Long,
        time: String,
        venue: String,
        maxParticipants: Int,
        isRegistrationOpen: Boolean,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            userRepository.getCurrentUser().onSuccess { user ->
                val eventId = eventRepository.generateEventId()
                var imageUrl: String? = null
                var imagePath: String? = null

                if (imageUri != null) {
                    Log.d("EVENT_UPLOAD", "Uploading banner for event: $eventId")
                    val uploadResult = eventRepository.uploadEventImage(eventId, imageUri)
                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()?.first
                        imagePath = uploadResult.getOrNull()?.second
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to upload banner image") }
                        return@launch
                    }
                }

                val event = Event(
                    id = eventId,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    imageStoragePath = imagePath,
                    organizerId = user.uid,
                    organizerName = user.fullName,
                    organizerRole = user.role.name,
                    venue = venue,
                    category = category,
                    collegeId = user.collegeId,
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    maxParticipants = maxParticipants,
                    isRegistrationOpen = isRegistrationOpen,
                    createdAt = System.currentTimeMillis()
                )

                eventRepository.createEvent(event)
                    .onSuccess {
                        Log.d("EVENT_CREATE", "Event created successfully: $eventId")
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    .onFailure { exception ->
                        _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to create event") }
                    }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to get user info") }
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
