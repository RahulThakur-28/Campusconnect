package com.rahul.campusconnect.presentation.placement.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
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
    private val placementRepository: PlacementRepository,
    private val storageManager: StorageManager
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

            placementRepository
                .getPlacementById(placementId)
                .onSuccess { placement ->

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
                }
                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Failed to load placement."
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

            try {

                var logoUrl = placement.logoUrl
                var logoStoragePath = placement.logoStoragePath

                if (logoUri != null) {

                    logoStoragePath =
                        StoragePathGenerator.placementLogo(
                            collegeId = placement.collegeId,
                            placementId = placement.id
                        )

                    val uploadResult =
                        storageManager.uploadImage(
                            bucket = StorageConstants.MEDIA_BUCKET,
                            path = logoStoragePath,
                            imageUri = logoUri
                        )

                    if (uploadResult.isFailure) {

                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                error = uploadResult.exceptionOrNull()?.message
                                    ?: "Failed to upload company logo."
                            )
                        }

                        return@launch
                    }

                    logoUrl = uploadResult.getOrThrow()
                }

                val updatedPlacement =
                    placement.copy(
                        logoUrl = logoUrl,
                        logoStoragePath = logoStoragePath,
                        updatedAt = System.currentTimeMillis()
                    )

                placementRepository
                    .updatePlacement(updatedPlacement)
                    .onSuccess {

                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                isSuccess = true,
                                placement = updatedPlacement,
                                error = null
                            )
                        }

                    }
                    .onFailure { exception ->

                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                error = exception.message
                                    ?: "Failed to update placement."
                            )
                        }
                    }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        error = e.message
                            ?: "Something went wrong."
                    )
                }

            } finally {

                isUpdating = false
            }
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