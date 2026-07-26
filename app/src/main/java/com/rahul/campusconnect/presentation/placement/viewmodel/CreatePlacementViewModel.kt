package com.rahul.campusconnect.presentation.placement.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.repository.PlacementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.placement.state.CreatePlacementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlacementViewModel @Inject constructor(
    private val placementRepository: PlacementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePlacementUiState())
    val uiState: StateFlow<CreatePlacementUiState> = _uiState.asStateFlow()

    fun createPlacement(
        placement: Placement,
        logoUri: Uri?,
        attachmentUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null, isSuccess = false) }

            try {
                val user = userRepository.getCurrentUser().getOrThrow()
                val placementId = placementRepository.generatePlacementId()

                var logoUrl = ""
                var logoPath = ""
                var attachmentUrl: String? = null
                var attachmentPath: String? = null

                // Upload Logo
                if (logoUri != null) {
                    Log.d("PLACEMENT_UPLOAD", "Uploading logo for placement: $placementId")
                    val result = placementRepository.uploadPlacementLogo(placementId, logoUri)
                    if (result.isSuccess) {
                        logoUrl = result.getOrThrow().first
                        logoPath = result.getOrThrow().second
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Logo upload failed")
                    }
                }

                // Upload Attachment
                if (attachmentUri != null) {
                    Log.d("PLACEMENT_UPLOAD", "Uploading attachment for placement: $placementId")
                    val result = placementRepository.uploadPlacementAttachment(placementId, attachmentUri, "pdf")
                    if (result.isSuccess) {
                        attachmentUrl = result.getOrThrow().first
                        attachmentPath = result.getOrThrow().second
                    } else {
                        // Cleanup logo if attachment fails
                        if (logoPath.isNotBlank()) placementRepository.deleteFile(logoPath)
                        throw result.exceptionOrNull() ?: Exception("Attachment upload failed")
                    }
                }

                val finalPlacement = placement.copy(
                    id = placementId,
                    logoUrl = logoUrl,
                    logoStoragePath = logoPath,
                    attachmentUrl = attachmentUrl,
                    attachmentStoragePath = attachmentPath,
                    createdBy = user.uid,
                    createdByName = user.fullName,
                    createdByRole = user.role.name,
                    collegeId = user.collegeId,
                    postedAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                placementRepository.createPlacement(finalPlacement).onSuccess {
                    Log.d("PLACEMENT_CREATE", "Placement created: $placementId")
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }.onFailure { e ->
                    // Cleanup uploaded files on failure
                    if (logoPath.isNotBlank()) placementRepository.deleteFile(logoPath)
                    if (attachmentPath != null) placementRepository.deleteFile(attachmentPath)
                    throw e
                }

            } catch (e: Exception) {
                Log.e("PLACEMENT_CREATE", "Error creating placement", e)
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to create placement") }
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
