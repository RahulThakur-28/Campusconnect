package com.rahul.campusconnect.presentation.placement.viewmodel


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.repository.PlacementRepository
import com.rahul.campusconnect.presentation.placement.state.EditPlacementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlacementViewModel @Inject constructor(
    private val placementRepository: PlacementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPlacementUiState())
    val uiState: StateFlow<EditPlacementUiState> = _uiState.asStateFlow()

    private var isUpdating = false

    fun loadPlacement(
        placementId: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            val result =
                placementRepository.getPlacementById(
                    placementId
                )

            result.onSuccess { placement ->

                if (placement == null) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Placement not found."
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            placement = placement,
                            error = null
                        )
                    }

                }

            }.onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = exception.message ?: "Failed to update placement."
                        )
                    }


            }

        }
    }

    fun updatePlacement(
        placement: Placement,
        logoUri: Uri?
    ) {

        if (isUpdating) return

        isUpdating = true

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    error = null
                )
            }

            val updatedPlacement =
                placement.copy(
                    updatedAt = System.currentTimeMillis()
                )

            val result =
                placementRepository.updatePlacement(
                    updatedPlacement
                )

            result.onSuccess {

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSuccess = true
                    )
                }

            }.onFailure {exception ->

                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = exception.message ?: "Failed to update placement."
                        )
                    }


            }

            isUpdating = false
        }
    }

    fun clearError() {

        _uiState.update {
            it.copy(
                error = null
            )
        }
    }

    fun resetSuccessState() {

        _uiState.update {
            it.copy(
                isSuccess = false
            )
        }
    }
}