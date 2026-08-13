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
            _uiState.update { it.copy(isSubmitting = true, error = null, isSuccess = false) }

            val oldLogoPath = currentPlacement.logoStoragePath
            val oldAttachmentPath = currentPlacement.attachmentStoragePath

            var logoUrl = currentPlacement.logoUrl
            var logoPath = currentPlacement.logoStoragePath
            var attachmentUrl = currentPlacement.attachmentUrl
            var attachmentPath = currentPlacement.attachmentStoragePath

            try {
                // ====================================================
                // LOGO HANDLING
                // ====================================================
                
                if (removeLogo && logoUri == null) {
                    logoUrl = ""
                    logoPath = ""
                }

                if (logoUri != null) {
                    Log.d("PLACEMENT_UPLOAD", "Uploading new logo for placement: ${currentPlacement.id}")
                    val result = placementRepository.uploadPlacementLogo(currentPlacement.collegeId, currentPlacement.id, logoUri)
                    if (result.isSuccess) {
                        logoUrl = result.getOrThrow().first
                        logoPath = result.getOrThrow().second
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Logo upload failed")
                    }
                }

                // ====================================================
                // ATTACHMENT HANDLING
                // ====================================================

                if (removeAttachment && attachmentUri == null) {
                    attachmentUrl = null
                    attachmentPath = null
                }

                if (attachmentUri != null) {
                    Log.d("PLACEMENT_UPLOAD", "Uploading new attachment for placement: ${currentPlacement.id}")
                    val result = placementRepository.uploadPlacementAttachment(currentPlacement.collegeId, currentPlacement.id, attachmentUri, "pdf")
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

                // ====================================================
                // FIRESTORE UPDATE
                // ====================================================

                placementRepository.updatePlacement(updatedPlacement).onSuccess {
                    Log.d("PLACEMENT_UPDATE", "Placement updated: ${currentPlacement.id}")
                    
                    // Cleanup newly orphaned logo
                    if (logoPath != oldLogoPath && oldLogoPath.isNotBlank()) {
                        placementRepository.deleteFile(oldLogoPath)
                    }

                    // Cleanup newly orphaned attachment
                    if (attachmentPath != oldAttachmentPath && oldAttachmentPath != null) {
                        placementRepository.deleteFile(oldAttachmentPath)
                    }

                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true, placement = updatedPlacement) }
                }.onFailure { e ->
                    // Rollback newly uploaded files if Firestore fails
                    if (logoPath != oldLogoPath && logoPath.isNotBlank()) {
                        placementRepository.deleteFile(logoPath)
                    }
                    if (attachmentPath != oldAttachmentPath && attachmentPath != null) {
                        placementRepository.deleteFile(attachmentPath)
                    }
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
