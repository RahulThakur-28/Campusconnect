package com.rahul.campusconnect.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var allUsers = listOf<User>()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        val collegeId = userRepository.currentUser.value?.collegeId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getUsersByCollege(collegeId)
                .onSuccess { users ->
                    allUsers = users
                    filterUsers()
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterUsers()
    }

    private fun filterUsers() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isBlank()) {
            allUsers
        } else {
            allUsers.filter { 
                it.fullName.lowercase().contains(query) || it.email.lowercase().contains(query)
            }
        }
        _uiState.update { it.copy(users = filtered) }
    }

    fun updateUserRole(user: User, newRole: UserRole) {
        val currentUser = userRepository.currentUser.value ?: return
        
        // Privilege escalation check
        if (newRole == UserRole.ADMIN && currentUser.role != UserRole.SUPER_ADMIN) return
        if (newRole == UserRole.SUPER_ADMIN) return // No one can create super admin from app

        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            userRepository.updateUserRole(user.uid, user.collegeId, newRole)
                .onSuccess {
                    loadUsers() // Refresh
                    _uiState.update { it.copy(isActionLoading = false, successMessage = "Role updated to ${newRole.displayName}") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isActionLoading = false, error = e.message) }
                }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(successMessage = null, error = null) }
}

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
