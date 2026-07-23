

package com.rahul.campusconnect.presentation.placement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.PlacementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.placement.state.PlacementDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacementDetailsViewModel @Inject constructor(
    private val placementRepository: PlacementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacementDetailsUiState())
    val uiState: StateFlow<PlacementDetailsUiState> = _uiState.asStateFlow()

    fun loadPlacement(placementId: String) {

        loadUserRole()

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            placementRepository.getPlacementById(placementId)
                .onSuccess { placement ->

                    _uiState.update {
                        it.copy(
                            placement = placement,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Unable to load placement."
                        )
                    }
                }
        }
    }

    private fun loadUserRole() {

        viewModelScope.launch {

            userRepository.getCurrentUser()
                .onSuccess { user ->

                    _uiState.update {
                        it.copy(
                            userRole = user.role
                        )
                    }
                }
        }
    }

    fun refresh() {

        _uiState.value.placement?.id?.let { placementId ->
            loadPlacement(placementId)
        }
    }

    fun clearError() {

        _uiState.update {
            it.copy(error = null)
        }
    }
}