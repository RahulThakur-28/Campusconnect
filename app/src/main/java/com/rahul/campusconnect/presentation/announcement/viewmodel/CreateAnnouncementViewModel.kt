package com.rahul.campusconnect.presentation.announcement.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.announcement.state.CreateAnnouncementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateAnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAnnouncementUiState())
    val uiState: StateFlow<CreateAnnouncementUiState> = _uiState.asStateFlow()

    fun createAnnouncement(
        title: String,
        description: String,
        category: String,
        imageUri: Uri?,
        attachmentUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            userRepository.getCurrentUser().onSuccess { user ->
                val announcementId = UUID.randomUUID().toString()
                var imageUrl: String? = null
                var imagePath: String? = null
                var attachmentUrl: String? = null
                var attachmentPath: String? = null

                // Handle Image Upload
                if (imageUri != null) {
                    val uploadResult = announcementRepository.uploadAnnouncementImage(announcementId, imageUri)
                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()?.first
                        imagePath = uploadResult.getOrNull()?.second
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}") }
                        return@launch
                    }
                }

                // Handle Attachment Upload
                if (attachmentUri != null) {
                    val uploadResult = announcementRepository.uploadAnnouncementAttachment(announcementId, attachmentUri)
                    if (uploadResult.isSuccess) {
                        attachmentUrl = uploadResult.getOrNull()?.first
                        attachmentPath = uploadResult.getOrNull()?.second
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to upload attachment: ${uploadResult.exceptionOrNull()?.message}") }
                        return@launch
                    }
                }

                val announcement = Announcement(
                    id = announcementId,
                    title = title,
                    description = description,
                    category = category,
                    imageUrl = imageUrl,
                    imageStoragePath = imagePath,
                    attachmentUrl = attachmentUrl,
                    attachmentStoragePath = attachmentPath,
                    hasAttachment = attachmentUrl != null,
                    postedBy = user.uid,
                    postedByName = user.fullName,
                    postedByRole = user.role.name,
                    isVerified = user.verificationStatus == "VERIFIED",
                    postedAt = System.currentTimeMillis(),
                    collegeId = user.collegeId
                )

                announcementRepository.createAnnouncement(announcement)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to publish announcement."
                            )
                        }
                    }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to get user info."
                    )
                }
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
