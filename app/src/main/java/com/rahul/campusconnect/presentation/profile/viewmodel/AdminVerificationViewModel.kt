package com.rahul.campusconnect.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.VerificationRequest
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.domain.repository.VerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminVerificationViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminVerificationUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val requests = combine(_selectedTab, userRepository.currentUser) { tab, user ->
        val collegeId = user?.collegeId
        if (collegeId == null) {
            emptyFlow<List<VerificationRequest>>()
        } else {
            val status = when (tab) {
                0 -> "PENDING"
                1 -> "APPROVED"
                else -> "REJECTED"
            }
            verificationRepository.getRequestsByStatus(collegeId, status)
        }
    }.flatMapLatest { it }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun approveRequest(request: VerificationRequest) {
        val adminId = userRepository.currentUser.value?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            verificationRepository.approveRequest(request, adminId)
                .onSuccess { _uiState.update { it.copy(isActionLoading = false, successMessage = "Approved successfully") } }
                .onFailure { e -> _uiState.update { it.copy(isActionLoading = false, error = e.message) } }
        }
    }

    fun rejectRequest(request: VerificationRequest, reason: String) {
        val adminId = userRepository.currentUser.value?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            verificationRepository.rejectRequest(request.collegeId, request.userId, reason, adminId)
                .onSuccess { _uiState.update { it.copy(isActionLoading = false, successMessage = "Rejected successfully") } }
                .onFailure { e -> _uiState.update { it.copy(isActionLoading = false, error = e.message) } }
        }
    }
    
    fun clearMessages() = _uiState.update { it.copy(successMessage = null, error = null) }
}

data class AdminVerificationUiState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
