package com.rahul.campusconnect.presentation.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.*
import com.rahul.campusconnect.presentation.profile.state.EditProfileUiState
import com.rahul.campusconnect.presentation.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val notesRepository: NotesRepository,
    private val eventRepository: EventRepository,
    private val placementRepository: PlacementRepository,
    private val announcementRepository: AnnouncementRepository,
    private val discussionRepository: DiscussionRepository,
    private val lostFoundRepository: LostFoundRepository,
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _editProfileState = MutableStateFlow(EditProfileUiState())
    val editProfileState = _editProfileState.asStateFlow()

    init {
        observeUserSession()
    }

    private fun observeUserSession() {
        userRepository.currentUser
            .onEach { user ->
                if (user != null) {
                    _uiState.update { it.copy(user = user, isLoading = false) }
                    _editProfileState.update {
                        it.copy(
                            fullName = user.fullName,
                            phoneNumber = user.phone,
                            bio = user.bio,
                            branch = user.department,
                            year = user.academicYear,
                            section = user.section ?: "",
                            profileImage = user.profileImage
                        )
                    }
                    loadMyContent(user.uid)
                    checkVerificationStatus(user.uid, user.collegeId)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkVerificationStatus(userId: String, collegeId: String) {
        viewModelScope.launch {
            verificationRepository.getRequestByUserId(userId, collegeId).onSuccess { request ->
                _uiState.update { it.copy(verificationRequest = request) }
            }
        }
    }

    private fun loadMyContent(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val notesDeferred = async { notesRepository.getMyNotes(userId) }
                val eventsDeferred = async { eventRepository.getMyEvents(userId) }
                val placementsDeferred = async { placementRepository.getMyPlacements(userId) }
                val announcementsDeferred = async { announcementRepository.getMyAnnouncements(userId) }
                val discussionsDeferred = async { discussionRepository.getMyDiscussions(userId) }
                val lostFoundDeferred = async { lostFoundRepository.getMyItems(userId) }

                val notes = notesDeferred.await().getOrDefault(emptyList())
                val events = eventsDeferred.await().getOrDefault(emptyList())
                val placements = placementsDeferred.await().getOrDefault(emptyList())
                val announcements = announcementsDeferred.await().getOrDefault(emptyList())
                val discussions = discussionsDeferred.await().getOrDefault(emptyList())
                val lostFound = lostFoundDeferred.await().getOrDefault(emptyList())

                _uiState.update { it.copy(
                    isLoading = false,
                    notesCount = notes.size,
                    eventsCount = events.size,
                    placementsCount = placements.size,
                    announcementsCount = announcements.size,
                    discussionsCount = discussions.size,
                    lostFoundItemsCount = lostFound.size,
                    myNotes = notes,
                    myEvents = events,
                    myPlacements = placements,
                    myAnnouncements = announcements,
                    myQuestions = discussions,
                    myLostFoundItems = lostFound
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load content") }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        val currentUser = _uiState.value.user
        if (currentUser.uid.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val oldPath = currentUser.profileImageStoragePath

            Log.d("PROFILE_UPLOAD", "Uploading new profile image for user: ${currentUser.uid}")
            userRepository.uploadProfileImage(currentUser.collegeId, currentUser.uid, uri)
                .onSuccess { (url, newPath) ->
                    val updatedUser = currentUser.copy(
                        profileImage = url,
                        profileImageStoragePath = newPath
                    )
                    
                    userRepository.updateProfile(updatedUser)
                        .onSuccess {
                            Log.d("PROFILE_UPLOAD", "Profile image updated in Firestore")
                            // Delete old image if it exists
                            if (oldPath != null && oldPath.isNotBlank()) {
                                userRepository.deleteFile(oldPath)
                            }
                            _uiState.update { it.copy(isLoading = false, successMessage = "Profile picture updated") }
                        }
                        .onFailure { e ->
                            Log.e("PROFILE_UPLOAD", "Firestore update failed, rolling back new image", e)
                            // Rollback newly uploaded image
                            userRepository.deleteFile(newPath)
                            _uiState.update { it.copy(isLoading = false, error = "Failed to update profile: ${e.message}") }
                        }
                }
                .onFailure { e ->
                    Log.e("PROFILE_UPLOAD", "Supabase upload failed", e)
                    _uiState.update { it.copy(isLoading = false, error = "Failed to upload image: ${e.message}") }
                }
        }
    }

    fun removeProfileImage() {
        val currentUser = _uiState.value.user
        val oldPath = currentUser.profileImageStoragePath ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val updatedUser = currentUser.copy(
                profileImage = "",
                profileImageStoragePath = null
            )

            userRepository.updateProfile(updatedUser)
                .onSuccess {
                    Log.d("PROFILE_IMAGE", "Image removed from Firestore, deleting file: $oldPath")
                    userRepository.deleteFile(oldPath)
                    _uiState.update { it.copy(isLoading = false, successMessage = "Profile picture removed") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Failed to remove image: ${e.message}") }
                }
        }
    }

    fun saveProfile() {
        val form = _editProfileState.value
        if (form.fullName.isBlank()) {
            _editProfileState.update { it.copy(error = "Name required") }
            return
        }

        viewModelScope.launch {
            _editProfileState.update { it.copy(isSaving = true, error = null) }
            val updatedUser = _uiState.value.user.copy(
                fullName = form.fullName,
                phone = form.phoneNumber,
                bio = form.bio,
                department = form.branch,
                academicYear = form.year,
                section = form.section,
                updatedAt = System.currentTimeMillis()
            )

            userRepository.updateProfile(updatedUser)
                .onSuccess {
                    _editProfileState.update { it.copy(isSaving = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _editProfileState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun onFullNameChange(v: String) = _editProfileState.update { it.copy(fullName = v) }
    fun onPhoneNumberChange(v: String) = _editProfileState.update { it.copy(phoneNumber = v) }
    fun onBioChange(v: String) = _editProfileState.update { it.copy(bio = v) }
    fun onBranchChange(v: String) = _editProfileState.update { it.copy(branch = v) }
    fun onYearChange(v: String) = _editProfileState.update { it.copy(year = v) }
    fun onSectionChange(v: String) = _editProfileState.update { it.copy(section = v) }
    
    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearEditError() = _editProfileState.update { it.copy(error = null) }
    fun resetSuccess() = _editProfileState.update { it.copy(isSuccess = false) }
    
    fun deleteDiscussion(discussionId: String) {
        viewModelScope.launch {
            discussionRepository.deleteQuestion(discussionId).onSuccess {
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            userRepository.loadUserSession().onSuccess {
                _uiState.value.user.let { 
                    loadMyContent(it.uid)
                    checkVerificationStatus(it.uid, it.collegeId)
                }
            }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
