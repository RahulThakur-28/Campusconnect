package com.rahul.campusconnect.presentation.event.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.presentation.event.state.EditEventUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
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

    fun updateEvent(
        title: String,
        description: String,
        category: String,
        startDate: Long,
        endDate: Long,
        time: String,
        venue: String,
        maxParticipants: Int,
        isRegistrationOpen: Boolean,
        imageUri: Uri?,
        removeImage: Boolean = false
    ) {
        val currentEvent = _uiState.value.event ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            var imageUrl = currentEvent.imageUrl
            var imagePath = currentEvent.imageStoragePath

            if (removeImage && imagePath != null) {
                eventRepository.deleteFile(imagePath)
                imageUrl = null
                imagePath = null
            }

            if (imageUri != null) {
                // Delete old image if exists
                if (imagePath != null) {
                    eventRepository.deleteFile(imagePath)
                }
                Log.d("EVENT_UPLOAD", "Uploading new banner for event: ${currentEvent.id}")
                val uploadResult = eventRepository.uploadEventImage(currentEvent.id, imageUri)
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrNull()?.first
                    imagePath = uploadResult.getOrNull()?.second
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to upload image") }
                    return@launch
                }
            }

            val updatedEvent = currentEvent.copy(
                title = title,
                description = description,
                category = category,
                venue = venue,
                startDate = startDate,
                endDate = endDate,
                time = time,
                maxParticipants = maxParticipants,
                isRegistrationOpen = isRegistrationOpen,
                imageUrl = imageUrl,
                imageStoragePath = imagePath,
                updatedAt = System.currentTimeMillis()
            )

            eventRepository.updateEvent(updatedEvent)
                .onSuccess {
                    Log.d("EVENT_UPDATE", "Event updated: ${currentEvent.id}")
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, event = updatedEvent) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
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
