package com.rahul.campusconnect.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.update { it.copy(currentPassword = password, error = null) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password, error = null) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun changePassword() {
        val state = _uiState.value
        if (!validate(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Reauthenticate
            val reauthResult = authRepository.reauthenticate(state.currentPassword)
            if (reauthResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = reauthResult.exceptionOrNull()?.message ?: "Reauthentication failed") }
                return@launch
            }

            // 2. Change Password
            val changeResult = authRepository.changePassword(state.newPassword)
            if (changeResult.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = changeResult.exceptionOrNull()?.message ?: "Failed to update password") }
            }
        }
    }

    private fun validate(state: ChangePasswordUiState): Boolean {
        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(error = "Current password is required") }
            return false
        }
        if (state.newPassword.length < 8) {
            _uiState.update { it.copy(error = "New password must be at least 8 characters") }
            return false
        }
        if (!state.newPassword.any { it.isUpperCase() } || !state.newPassword.any { it.isLowerCase() } || !state.newPassword.any { it.isDigit() }) {
            _uiState.update { it.copy(error = "Password must contain uppercase, lowercase and a digit") }
            return false
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return false
        }
        return true
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
