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
                            email = it.email,
                            role = it.role,
                            department = it.department,
                            academicYear = it.academicYear,
                            collegeName = it.collegeName,
                            isVerified = it.isVerified,
                            profilePictureUrl = it.profileImage
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}
