package com.rahul.campusconnect.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.profile.state.EditProfileUiState
import com.rahul.campusconnect.presentation.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private val _editProfileState = MutableStateFlow(EditProfileUiState())
    val editProfileState = _editProfileState.asStateFlow()


    init {
        getCurrentUser()
    }

    fun getCurrentUser() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            userRepository.getCurrentUser()
                .onSuccess { user ->

                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            error = null
                        )
                    }

                    _editProfileState.value = EditProfileUiState(
                        fullName = user.fullName,
                        phoneNumber = user.phoneNumber,
                        bio = user.bio,
                        branch = user.branch,
                        year = user.year,
                        section = user.section,
                        profileImage = user.profileImage
                    )

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

    fun updateUser(user: User) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            userRepository.updateUser(user)
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            error = null
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

    //-------------------edit profile ui finction -----------------------------------

    fun updateEditProfileState(
        state: EditProfileUiState
    ) {
        _editProfileState.value = state
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun onFullNameChange(value: String) {
        _editProfileState.update {
            it.copy(fullName = value)
        }
    }

    fun onPhoneNumberChange(value: String) {
        _editProfileState.update {
            it.copy(phoneNumber = value)
        }
    }

    fun onBioChange(value: String) {
        _editProfileState.update {
            it.copy(bio = value)
        }
    }

    fun onBranchChange(value: String) {
        _editProfileState.update {
            it.copy(branch = value)
        }
    }

    fun onYearChange(value: String) {
        _editProfileState.update {
            it.copy(year = value)
        }
    }

    fun onSectionChange(value: String) {
        _editProfileState.update {
            it.copy(section = value)
        }
    }

    fun onSuccessConsumed() {
        _editProfileState.value = _editProfileState.value.copy(
            isSuccess = false
        )
    }

    fun saveProfile() {

        val form = editProfileState.value

        if (form.fullName.isBlank()) {

            _editProfileState.update {
                it.copy(error = "Full name cannot be empty")
            }
            return
        }

        viewModelScope.launch {

            _editProfileState.update {
                it.copy(
                    isSaving = true,
                    error = null
                )
            }

            val currentUser = uiState.value.user

            val updatedUser = currentUser.copy(
                fullName = form.fullName,
                phoneNumber = form.phoneNumber,
                bio = form.bio,
                branch = form.branch,
                year = form.year,
                section = form.section,
                profileImage = form.profileImage,
                updatedAt = System.currentTimeMillis()
            )

            userRepository.updateUser(updatedUser)
                .onSuccess {

                    _uiState.update {
                        it.copy(user = updatedUser)
                    }

                    _editProfileState.value = _editProfileState.value.copy(
                        isSaving = false,
                        error = null,
                        isSuccess = true
                    )

                }
                .onFailure { exception ->

                    _editProfileState.update {
                        it.copy(
                            isSaving = false,
                            error = exception.message
                                ?: "Something went wrong"
                        )
                    }

                }
        }
    }
}