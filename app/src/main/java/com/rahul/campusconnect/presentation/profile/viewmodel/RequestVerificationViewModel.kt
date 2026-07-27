package com.rahul.campusconnect.presentation.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.model.VerificationRequest
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.domain.repository.VerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestVerificationViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestVerificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        userRepository.currentUser
            .onEach { user ->
                if (user != null) {
                    _uiState.update { it.copy(userRole = user.role) }
                    checkExistingRequest(user.uid, user.collegeId)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkExistingRequest(userId: String, collegeId: String) {
        viewModelScope.launch {
            verificationRepository.getRequestByUserId(userId, collegeId).onSuccess { request ->
                _uiState.update { it.copy(existingRequest = request) }
            }
        }
    }

    fun submitRequest(
        idNumber: String, // Enrollment or Employee ID
        department: String,
        academicYear: String?,
        documentUri: Uri,
        requestedRole: UserRole
    ) {
        val user = userRepository.currentUser.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = VerificationRequest(
                userId = user.uid,
                userName = user.fullName,
                userEmail = user.email,
                collegeId = user.collegeId,
                currentRole = user.role,
                requestedRole = requestedRole,
                department = department,
                academicYear = academicYear,
                enrollmentNumber = if (requestedRole == UserRole.VERIFIED_STUDENT) idNumber else null,
                employeeId = if (requestedRole == UserRole.VERIFIED_TEACHER) idNumber else null,
                status = "PENDING"
            )
            
            verificationRepository.submitVerificationRequest(request, documentUri)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
    
    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class RequestVerificationUiState(
    val userRole: UserRole = UserRole.STUDENT,
    val existingRequest: VerificationRequest? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
