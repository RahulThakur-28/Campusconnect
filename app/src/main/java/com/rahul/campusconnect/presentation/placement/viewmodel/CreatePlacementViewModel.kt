package com.rahul.campusconnect.presentation.placement.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
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
    private val userRepository: UserRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePlacementUiState())
    val uiState: StateFlow<CreatePlacementUiState> = _uiState.asStateFlow()

    private var isCreatingPlacement = false

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun resetSuccessState() {
        _uiState.update {
            it.copy(isSuccess = false)
        }
    }

    private fun setSubmitting(value: Boolean) {
        _uiState.update {
            it.copy(isSubmitting = value)
        }
    }

    private fun setSuccess() {
        _uiState.update {
            it.copy(
                isSubmitting = false,
                isSuccess = true,
                error = null
            )
        }
    }

    private fun setError(message: String) {
        _uiState.update {
            it.copy(
                isSubmitting = false,
                error = message
            )
        }
    }

    fun createPlacement(
        placement: Placement,
        logoUri: Uri?
    ) {

        if (isCreatingPlacement) return

        isCreatingPlacement = true

        viewModelScope.launch {

            setSubmitting(true)

            var uploadedLogoPath = ""
            var uploadedLogoUrl = ""

            try {

                val userResult = userRepository.getCurrentUser()

                if (userResult.isFailure) {
                    setError(
                        userResult.exceptionOrNull()?.message
                            ?: "Unable to load user."
                    )
                    return@launch
                }

                val user = userResult.getOrThrow()

                val placementId =
                    placementRepository.generatePlacementId()

                if (logoUri != null) {

                    uploadedLogoPath =
                        StoragePathGenerator.placementLogo(
                            collegeId = user.collegeId,
                            placementId = placementId
                        )

                    val uploadResult =
                        storageManager.uploadImage(
                            bucket = StorageConstants.MEDIA_BUCKET,
                            path = uploadedLogoPath,
                            imageUri = logoUri
                        )

                    if (uploadResult.isFailure) {

                        setError(
                            uploadResult.exceptionOrNull()?.message
                                ?: "Failed to upload company logo."
                        )

                        return@launch
                    }

                    uploadedLogoUrl =
                        uploadResult.getOrThrow()
                }

                val finalPlacement =
                    placement.copy(

                        id = placementId,

                        logoUrl = uploadedLogoUrl,

                        logoStoragePath = uploadedLogoPath,

                        postedAt = System.currentTimeMillis(),

                        updatedAt = System.currentTimeMillis(),

                        createdBy = user.uid,

                        createdByName = user.fullName,

                        createdByRole = user.role.name,

                        collegeId = user.collegeId,

                        isDeleted = false
                    )

                val createResult =
                    placementRepository.createPlacement(
                        finalPlacement
                    )

                if (createResult.isFailure) {

                    if (uploadedLogoPath.isNotBlank()) {

                        storageManager.deleteFile(
                            bucket = StorageConstants.MEDIA_BUCKET,
                            path = uploadedLogoPath
                        )
                    }

                    setError(
                        createResult.exceptionOrNull()?.message
                            ?: "Failed to create placement."
                    )

                    return@launch
                }

                setSuccess()

            } catch (e: Exception) {

                if (uploadedLogoPath.isNotBlank()) {

                    storageManager.deleteFile(
                        bucket = StorageConstants.MEDIA_BUCKET,
                        path = uploadedLogoPath
                    )
                }

                setError(
                    e.message ?: "Something went wrong."
                )

            } finally {

                isCreatingPlacement = false
            }
        }
    }
}