package com.rahul.campusconnect.presentation.auth.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value) }
    fun onCollegeIdChange(value: String) = _uiState.update { it.copy(collegeId = value) }
    fun onEnrollmentNumberChange(value: String) = _uiState.update { it.copy(enrollmentNumber = value) }
    fun onDepartmentChange(value: String) = _uiState.update { it.copy(department = value) }
    fun onAcademicYearChange(value: String) = _uiState.update { it.copy(academicYear = value) }
    fun onSectionChange(value: String) = _uiState.update { it.copy(section = value) }
    fun onProfileImageChange(uri: Uri?) = _uiState.update { it.copy(profileImage = uri) }

    fun register() {
        val state = _uiState.value
        if (!validateInput(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. Validate College ID
            val collegeResult = userRepository.validateCollegeId(state.collegeId)

            if (collegeResult.isFailure) {

                val message = collegeResult.exceptionOrNull()?.message
                    ?: "Unable to verify college."

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }

                return@launch
            }

            val college = collegeResult.getOrNull()

            if (college == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "College ID does not exist."
                    )
                }
                return@launch
            }

            // 2. Validate Enrollment Number Uniqueness
            val enrollmentResult = userRepository.isEnrollmentRegistered(state.collegeId, state.enrollmentNumber)
            if (enrollmentResult.getOrDefault(false)) {
                _uiState.update { it.copy(isLoading = false, error = "Enrollment Number already registered.") }
                return@launch
            }

            // 3. Optional: Upload Profile Image
            var profileImageUrl = ""
            var profileImagePath: String? = null
            state.profileImage?.let { uri ->
                val uploadResult = userRepository.uploadProfileImage(state.collegeId, "temp_${UUID.randomUUID()}", uri)
                if (uploadResult.isSuccess) {
                    profileImageUrl = uploadResult.getOrThrow().first
                    profileImagePath = uploadResult.getOrThrow().second
                }
            }

            // 4. Register in Auth and Save User
            val user = User(
                fullName = state.fullName,
                email = state.email,
                collegeId = state.collegeId,
                collegeName = college.collegeName,
                enrollmentNumber = state.enrollmentNumber,
                department = state.department,
                academicYear = state.academicYear,
                section = state.section,
                profileImage = profileImageUrl,
                profileImageStoragePath = profileImagePath,
                role = UserRole.STUDENT,
                verificationStatus = "PENDING",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = authRepository.register(user, state.password)
            
            // If registration fails and we uploaded an image, cleanup
            if (result.isFailure && profileImagePath != null) {
                userRepository.deleteFile(profileImagePath)
            }
            
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    isSuccess = result.isSuccess,
                    error = result.exceptionOrNull()?.message
                ) 
            }
        }
    }

    private fun validateInput(state: RegisterUiState): Boolean {
        if (state.fullName.isBlank()) { setError("Full Name is required"); return false }
        if (state.email.isBlank()) { setError("College Email is required"); return false }
        if (state.password.length < 6) { setError("Password must be at least 6 characters"); return false }
        if (state.password != state.confirmPassword) { setError("Passwords do not match"); return false }
        if (state.collegeId.length != 8 || state.collegeId.toLongOrNull() == null) { 
            setError("College ID must be exactly 8 digits"); return false 
        }
        if (state.enrollmentNumber.isBlank()) { setError("Enrollment Number is required"); return false }
        if (state.department.isBlank()) { setError("Department is required"); return false }
        if (state.academicYear.isBlank()) { setError("Academic Year is required"); return false }
        return true
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
