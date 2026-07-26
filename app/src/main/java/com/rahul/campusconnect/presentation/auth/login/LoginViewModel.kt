package com.rahul.campusconnect.presentation.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.PreferenceManager
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    fun login(collegeId: String, email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            // 1. Save collegeId to preferences first so AuthRepository can find it
            preferenceManager.saveCollegeId(collegeId)

            // 2. Validate college exists
            val collegeResult = userRepository.validateCollegeId(collegeId)
            if (collegeResult.isFailure || collegeResult.getOrNull() == null) {
                isLoading = false
                errorMessage = "Invalid College ID"
                return@launch
            }

            // 3. Login
            val result = authRepository.login(email, password)

            if (result.isSuccess) {
                loginSuccess = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
            }
            isLoading = false
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
