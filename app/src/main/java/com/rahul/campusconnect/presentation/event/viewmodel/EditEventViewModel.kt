package com.rahul.campusconnect.presentation.event.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Event
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

    private val eventRepository: EventRepository,

    private val storageManager: StorageManager

) : ViewModel() {

    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> = _uiState.asStateFlow()

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

    fun updateEvent(
        event: Event,
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

            var imageUrl = event.imageUrl

            if (imageUri != null) {

                storageManager.uploadImage(
                    bucket = StorageConstants.MEDIA_BUCKET,
                    path = "${StorageConstants.EVENTS}/event_${System.currentTimeMillis()}.jpg",
                    imageUri = imageUri
                )
                    .onSuccess {

                        imageUrl = it

                    }
                    .onFailure { exception ->

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }

                        return@launch
                    }
            }

            val updatedEvent = event.copy(
                imageUrl = imageUrl
            )

            eventRepository.updateEvent(updatedEvent)
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            event = updatedEvent
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