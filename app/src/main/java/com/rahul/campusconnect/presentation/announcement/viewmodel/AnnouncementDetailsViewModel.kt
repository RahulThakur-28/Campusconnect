package com.rahul.campusconnect.presentation.announcement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.announcement.state.AnnouncementDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementDetailsUiState())
    val uiState: StateFlow<AnnouncementDetailsUiState> = _uiState.asStateFlow()

    fun loadAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            userRepository.getCurrentUser().onSuccess { user ->
                _uiState.update { it.copy(userRole = user.role, currentUserId = user.uid) }
            }

            announcementRepository.getAnnouncementById(announcementId)
                .onSuccess { announcement ->
                    if (announcement == null) {
                        _uiState.update { 
                            it.copy(isLoading = false, error = "Announcement not found or has been deleted.") 
                        }
                    } else {
                        _uiState.update { it.copy(announcement = announcement, isLoading = false) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load announcement."
                        )
                    }
                }
        }
    }

    fun deleteAnnouncement() {
        val announcementId = _uiState.value.announcement?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            announcementRepository.deleteAnnouncement(announcementId)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            error = exception.message ?: "Failed to delete announcement."
                        )
                    }
                }
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
