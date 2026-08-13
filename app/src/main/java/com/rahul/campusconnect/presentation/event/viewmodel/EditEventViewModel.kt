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

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            eventRepository.getEventById(eventId)
                .onSuccess { event ->

                    if (event == null) {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Event not found"
                            )
                        }

                    } else {

                        _uiState.update {
                            it.copy(
                                event = event,
                                isLoading = false
                            )
                        }
                    }
                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load event"
                        )
                    }
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

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
            }

            val oldImagePath = currentEvent.imageStoragePath

            var imageUrl = currentEvent.imageUrl
            var imagePath = currentEvent.imageStoragePath

            // ====================================================
            // CASE 1: REMOVE EXISTING IMAGE
            // ====================================================

            if (removeImage && imageUri == null) {

                imageUrl = null
                imagePath = null
            }

            // ====================================================
            // CASE 2: NEW IMAGE SELECTED
            // ====================================================

            if (imageUri != null) {

                Log.d(
                    "EVENT_UPLOAD",
                    "Uploading new banner for event: ${currentEvent.id}"
                )

                /*
                 * IMPORTANT:
                 *
                 * Do NOT delete the old image before upload.
                 *
                 * New upload uses a NEW storage path.
                 */

                val uploadResult =
                    eventRepository.uploadEventImage(
                        currentEvent.collegeId,
                        currentEvent.id,
                        imageUri
                    )

                if (uploadResult.isFailure) {

                    val exception =
                        uploadResult.exceptionOrNull()

                    Log.e(
                        "EVENT_UPLOAD",
                        "New banner upload failed. Error: ${exception?.message}",
                        exception
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to upload image: ${exception?.message ?: "Unknown error"}"
                        )
                    }

                    return@launch
                }

                val uploadedFile =
                    uploadResult.getOrNull()

                if (uploadedFile == null) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Image upload returned no file"
                        )
                    }

                    return@launch
                }

                imageUrl = uploadedFile.first
                imagePath = uploadedFile.second

                Log.d(
                    "EVENT_UPLOAD",
                    "New banner uploaded successfully"
                )

                Log.d(
                    "EVENT_UPLOAD",
                    "New path: $imagePath"
                )
            }

            // ====================================================
            // CREATE UPDATED EVENT
            // ====================================================

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

            // ====================================================
            // UPDATE FIRESTORE
            // ====================================================

            val updateResult =
                eventRepository.updateEvent(updatedEvent)

            if (updateResult.isFailure) {

                val exception =
                    updateResult.exceptionOrNull()

                Log.e(
                    "EVENT_UPDATE",
                    "Firestore event update failed",
                    exception
                )

                /*
                 * IMPORTANT:
                 *
                 * If a new image was uploaded but Firestore update
                 * failed, remove the new image to avoid orphan files.
                 */

                if (
                    imageUri != null &&
                    imagePath != null &&
                    imagePath != oldImagePath
                ) {
                    eventRepository.deleteFile(imagePath)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception?.message
                            ?: "Failed to update event"
                    )
                }

                return@launch
            }

            // ====================================================
            // DELETE OLD IMAGE ONLY AFTER SUCCESSFUL UPDATE
            // ====================================================

            if (
                oldImagePath != null &&
                oldImagePath != imagePath
            ) {

                Log.d(
                    "EVENT_UPLOAD",
                    "Deleting old banner: $oldImagePath"
                )

                val deleteResult =
                    eventRepository.deleteFile(oldImagePath)

                deleteResult
                    .onSuccess {
                        Log.d(
                            "EVENT_UPLOAD",
                            "Old banner deleted successfully"
                        )
                    }
                    .onFailure { exception ->
                        /*
                         * Do not fail the entire event update here.
                         *
                         * Firestore already contains the new image/state.
                         * Old file can be cleaned later.
                         */
                        Log.w(
                            "EVENT_UPLOAD",
                            "Old banner cleanup failed. Error: ${exception.message}",
                            exception
                        )
                    }
            }

            // ====================================================
            // SUCCESS
            // ====================================================

            Log.d(
                "EVENT_UPDATE",
                "Event updated successfully: ${currentEvent.id}"
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    event = updatedEvent,
                    successMessage = "Event Updated Successfully"
                )
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update {
            it.copy(isSuccess = false)
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }
}