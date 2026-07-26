package com.rahul.campusconnect.presentation.more.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.more.state.MoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoreUiState())
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        userRepository.currentUser
            .onEach { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            userName = it.fullName,
                            role = it.role.displayName,
                            department = it.department,
                            academicYear = it.academicYear,
                            isVerified = it.verificationStatus == "VERIFIED",
                            profilePictureUrl = it.profileImage
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
