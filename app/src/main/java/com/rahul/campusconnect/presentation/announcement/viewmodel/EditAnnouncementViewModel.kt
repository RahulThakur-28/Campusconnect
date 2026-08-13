package com.rahul.campusconnect.presentation.announcement.viewmodel

import android.net.Uri
import android.util.Log
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
            _uiState.update { it.copy(isSubmitting = true, error = null, isSuccess = false) }
            
            val oldImagePath = currentAnnouncement.imageStoragePath
            val oldAttachmentPath = currentAnnouncement.attachmentStoragePath

            var imageUrl = currentAnnouncement.imageUrl
            var imagePath = currentAnnouncement.imageStoragePath
            var attachmentUrl = currentAnnouncement.attachmentUrl
            var attachmentPath = currentAnnouncement.attachmentStoragePath

            try {
                // ====================================================
                // IMAGE HANDLING
                // ====================================================

                if (removeImage && imageUri == null) {
                    imageUrl = null
                    imagePath = null
                }

                if (imageUri != null) {
                    Log.d("ANNOUNCEMENT_UPLOAD", "Uploading new banner for announcement: ${currentAnnouncement.id}")
                    val uploadResult = announcementRepository.uploadAnnouncementImage(currentAnnouncement.collegeId, currentAnnouncement.id, imageUri)
                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()?.first
                        imagePath = uploadResult.getOrNull()?.second
                    } else {
                        throw uploadResult.exceptionOrNull() ?: Exception("Image upload failed")
                    }
                }

                // ====================================================
                // ATTACHMENT HANDLING
                // ====================================================

                if (removeAttachment && attachmentUri == null) {
                    attachmentUrl = null
                    attachmentPath = null
                }

                if (attachmentUri != null) {
                    Log.d("ANNOUNCEMENT_UPLOAD", "Uploading new attachment for announcement: ${currentAnnouncement.id}")
                    val uploadResult = announcementRepository.uploadAnnouncementAttachment(currentAnnouncement.collegeId, currentAnnouncement.id, attachmentUri)
                    if (uploadResult.isSuccess) {
                        attachmentUrl = uploadResult.getOrNull()?.first
                        attachmentPath = uploadResult.getOrNull()?.second
                    } else {
                        throw uploadResult.exceptionOrNull() ?: Exception("Attachment upload failed")
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

                // ====================================================
                // FIRESTORE UPDATE
                // ====================================================

                announcementRepository.updateAnnouncement(updatedAnnouncement)
                    .onSuccess {
                        Log.d("ANNOUNCEMENT_UPDATE", "Announcement updated successfully: ${currentAnnouncement.id}")

                        // Cleanup old image
                        if (imagePath != oldImagePath && oldImagePath != null) {
                            announcementRepository.deleteFile(oldImagePath)
                        }

                        // Cleanup old attachment
                        if (attachmentPath != oldAttachmentPath && oldAttachmentPath != null) {
                            announcementRepository.deleteFile(oldAttachmentPath)
                        }

                        _uiState.update { it.copy(isSubmitting = false, isSuccess = true, announcement = updatedAnnouncement) }
                    }
                    .onFailure { exception ->
                        // Rollback newly uploaded files
                        if (imagePath != oldImagePath && imagePath != null) {
                            announcementRepository.deleteFile(imagePath)
                        }
                        if (attachmentPath != oldAttachmentPath && attachmentPath != null) {
                            announcementRepository.deleteFile(attachmentPath)
                        }
                        throw exception
                    }

            } catch (e: Exception) {
                Log.e("ANNOUNCEMENT_UPDATE", "Error updating announcement", e)
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to update announcement") }
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
