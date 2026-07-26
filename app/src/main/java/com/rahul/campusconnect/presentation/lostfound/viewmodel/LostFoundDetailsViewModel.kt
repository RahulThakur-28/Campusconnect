package com.rahul.campusconnect.presentation.lostfound.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostFoundDetailsViewModel @Inject constructor(
    private val lostFoundRepository: LostFoundRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LostFoundDetailsUiState())
    val uiState: StateFlow<LostFoundDetailsUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            userRepository.getCurrentUser().onSuccess { user ->
                _uiState.update { it.copy(userRole = user.role, currentUserId = user.uid) }
            }

            lostFoundRepository.getItemById(itemId)
                .onSuccess { item ->
                    if (item == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Item not found") }
                    } else {
                        _uiState.update { it.copy(item = item, isLoading = false) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun deleteItem() {
        val item = _uiState.value.item ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            
            lostFoundRepository.deleteItem(item.id, item.imageStoragePath)
                .onSuccess {
                    Log.d("LOST_FOUND_DELETE", "Item deleted: ${item.id}")
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isDeleting = false, error = exception.message) }
                }
        }
    }

    fun markAsResolved() {
        val itemId = _uiState.value.item?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isResolving = true) }
            lostFoundRepository.markAsResolved(itemId)
                .onSuccess {
                    Log.d("LOST_FOUND_RESOLVE", "Item resolved: $itemId")
                    _uiState.update { it.copy(isResolving = false) }
                    loadItem(itemId) // Refresh details
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isResolving = false, error = exception.message) }
                }
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
