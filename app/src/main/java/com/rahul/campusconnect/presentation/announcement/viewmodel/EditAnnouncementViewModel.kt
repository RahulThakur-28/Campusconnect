package com.rahul.campusconnect.presentation.announcement.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import com.rahul.campusconnect.presentation.announcement.state.EditAnnouncementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAnnouncementUiState())
    val uiState: StateFlow<EditAnnouncementUiState> = _uiState.asStateFlow()

    fun loadAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            announcementRepository.getAnnouncementById(announcementId)
                .onSuccess { announcement ->
                    if (announcement == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Announcement not found") }
                    } else {
                        _uiState.update { it.copy(announcement = announcement, isLoading = false) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun updateAnnouncement(
        title: String,
        description: String,
        category: String,
        imageUri: Uri?,
        attachmentUri: Uri?,
        removeImage: Boolean = false,
        removeAttachment: Boolean = false
    ) {
        val currentAnnouncement = _uiState.value.announcement ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            
            var imageUrl = currentAnnouncement.imageUrl
            var imagePath = currentAnnouncement.imageStoragePath
            var attachmentUrl = currentAnnouncement.attachmentUrl
            var attachmentPath = currentAnnouncement.attachmentStoragePath

            // Handle Image Removal
            if (removeImage && imagePath != null) {
                announcementRepository.deleteFile(imagePath)
                imageUrl = null
                imagePath = null
            }

            // Handle New Image Upload
            if (imageUri != null) {
                // Delete old one if exists
                if (imagePath != null) {
                    announcementRepository.deleteFile(imagePath)
                }
                val uploadResult = announcementRepository.uploadAnnouncementImage(currentAnnouncement.id, imageUri)
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrNull()?.first
                    imagePath = uploadResult.getOrNull()?.second
                } else {
                    _uiState.update { it.copy(isSubmitting = false, error = "Failed to upload image") }
                    return@launch
                }
            }

            // Handle Attachment Removal
            if (removeAttachment && attachmentPath != null) {
                announcementRepository.deleteFile(attachmentPath)
                attachmentUrl = null
                attachmentPath = null
            }

            // Handle New Attachment Upload
            if (attachmentUri != null) {
                if (attachmentPath != null) {
                    announcementRepository.deleteFile(attachmentPath)
                }
                val uploadResult = announcementRepository.uploadAnnouncementAttachment(currentAnnouncement.id, attachmentUri)
                if (uploadResult.isSuccess) {
                    attachmentUrl = uploadResult.getOrNull()?.first
                    attachmentPath = uploadResult.getOrNull()?.second
                } else {
                    _uiState.update { it.copy(isSubmitting = false, error = "Failed to upload attachment") }
                    return@launch
                }
            }

            val updatedAnnouncement = currentAnnouncement.copy(
                title = title,
                description = description,
                category = category,
                imageUrl = imageUrl,
                imageStoragePath = imagePath,
                attachmentUrl = attachmentUrl,
                attachmentStoragePath = attachmentPath,
                hasAttachment = attachmentUrl != null,
                updatedAt = System.currentTimeMillis()
            )

            announcementRepository.updateAnnouncement(updatedAnnouncement)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isSubmitting = false, error = exception.message) }
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
