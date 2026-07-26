package com.rahul.campusconnect.presentation.placement.viewmodel

import android.net.Uri
import android.util.Log
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

    fun loadPlacement(placementId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            placementRepository.getPlacementById(placementId)
                .onSuccess { placement ->
                    if (placement == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Placement not found.") }
                    } else {
                        _uiState.update { it.copy(isLoading = false, placement = placement) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to load placement.") }
                }
        }
    }

    fun updatePlacement(
        placement: Placement,
        logoUri: Uri?,
        attachmentUri: Uri?,
        removeLogo: Boolean,
        removeAttachment: Boolean
    ) {
        val currentPlacement = _uiState.value.placement ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            try {
                var logoUrl = currentPlacement.logoUrl
                var logoPath = currentPlacement.logoStoragePath
                var attachmentUrl = currentPlacement.attachmentUrl
                var attachmentPath = currentPlacement.attachmentStoragePath

                // Handle Logo Update
                if (removeLogo && logoPath.isNotBlank()) {
                    placementRepository.deleteFile(logoPath)
                    logoUrl = ""
                    logoPath = ""
                }

                if (logoUri != null) {
                    if (logoPath.isNotBlank()) placementRepository.deleteFile(logoPath)
                    Log.d("PLACEMENT_UPLOAD", "Uploading new logo for placement: ${placement.id}")
                    val result = placementRepository.uploadPlacementLogo(placement.id, logoUri)
                    if (result.isSuccess) {
                        logoUrl = result.getOrThrow().first
                        logoPath = result.getOrThrow().second
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Logo upload failed")
                    }
                }

                // Handle Attachment Update
                if (removeAttachment && attachmentPath != null) {
                    placementRepository.deleteFile(attachmentPath)
                    attachmentUrl = null
                    attachmentPath = null
                }

                if (attachmentUri != null) {
                    if (attachmentPath != null) placementRepository.deleteFile(attachmentPath)
                    Log.d("PLACEMENT_UPLOAD", "Uploading new attachment for placement: ${placement.id}")
                    val result = placementRepository.uploadPlacementAttachment(placement.id, attachmentUri, "pdf")
                    if (result.isSuccess) {
                        attachmentUrl = result.getOrThrow().first
                        attachmentPath = result.getOrThrow().second
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Attachment upload failed")
                    }
                }

                val updatedPlacement = placement.copy(
                    logoUrl = logoUrl,
                    logoStoragePath = logoPath,
                    attachmentUrl = attachmentUrl,
                    attachmentStoragePath = attachmentPath,
                    updatedAt = System.currentTimeMillis()
                )

                placementRepository.updatePlacement(updatedPlacement).onSuccess {
                    Log.d("PLACEMENT_UPDATE", "Placement updated: ${placement.id}")
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true, placement = updatedPlacement) }
                }.onFailure { e ->
                    throw e
                }

            } catch (e: Exception) {
                Log.e("PLACEMENT_UPDATE", "Error updating placement", e)
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to update placement") }
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
