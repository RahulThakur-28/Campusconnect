package com.rahul.campusconnect.presentation.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.profile.state.EditProfileUiState
import com.rahul.campusconnect.presentation.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
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
                }
            }
            .launchIn(viewModelScope)
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.uploadProfileImage(uri)
                .onSuccess { url ->
                    val currentUser = _uiState.value.user
                    val updatedUser = currentUser.copy(profileImage = url)
                    userRepository.updateProfile(updatedUser)
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false, successMessage = "Image Updated") }
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.message) }
                        }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
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
}
