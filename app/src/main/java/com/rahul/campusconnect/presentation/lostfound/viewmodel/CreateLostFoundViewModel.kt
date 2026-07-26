package com.rahul.campusconnect.presentation.lostfound.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.lostfound.state.CreateLostFoundUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateLostFoundViewModel @Inject constructor(
    private val lostFoundRepository: LostFoundRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateLostFoundUiState())
    val uiState: StateFlow<CreateLostFoundUiState> = _uiState.asStateFlow()

    fun createReport(
        title: String,
        description: String,
        category: String,
        type: String,
        location: String,
        contactEmail: String,
        contactPhone: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            userRepository.getCurrentUser().onSuccess { user ->
                val itemId = UUID.randomUUID().toString()
                var imageUrl: String? = null
                var imagePath: String? = null

                if (imageUri != null) {
                    Log.d("LOST_FOUND_UPLOAD", "Uploading image for item: $itemId")
                    val uploadResult = lostFoundRepository.uploadImage(itemId, imageUri)
                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()?.first
                        imagePath = uploadResult.getOrNull()?.second
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to upload image") }
                        return@launch
                    }
                }

                val item = LostFoundItem(
                    id = itemId,
                    title = title,
                    description = description,
                    category = category,
                    type = type,
                    status = "ACTIVE",
                    ownerId = user.uid,
                    ownerName = user.fullName,
                    ownerRole = user.role.name,
                    contactEmail = contactEmail,
                    contactPhone = contactPhone,
                    collegeId = user.collegeId,
                    location = location,
                    imageUrl = imageUrl,
                    imageStoragePath = imagePath,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                lostFoundRepository.createItem(item)
                    .onSuccess {
                        Log.d("LOST_FOUND_CREATE", "Item report created: $itemId")
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to create report."
                            )
                        }
                    }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to get user info."
                    )
                }
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
