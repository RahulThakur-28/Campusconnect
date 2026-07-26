package com.rahul.campusconnect.presentation.lostfound.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import com.rahul.campusconnect.presentation.lostfound.state.EditLostFoundUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLostFoundViewModel @Inject constructor(
    private val lostFoundRepository: LostFoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditLostFoundUiState())
    val uiState: StateFlow<EditLostFoundUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lostFoundRepository.getItemById(itemId)
                .onSuccess { item ->
                    if (item == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Item not found") }
                    } else {
                        _uiState.update { it.copy(item = item, isLoading = false) }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateItem(
        title: String,
        description: String,
        category: String,
        type: String,
        location: String,
        contactEmail: String,
        contactPhone: String?,
        newImageUri: Uri? = null,
        removeImage: Boolean = false
    ) {
        val currentItem = _uiState.value.item ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            var imageUrl = currentItem.imageUrl
            var imagePath = currentItem.imageStoragePath

            if (removeImage && imagePath != null) {
                lostFoundRepository.deleteFile(imagePath)
                imageUrl = null
                imagePath = null
            }

            if (newImageUri != null) {
                if (imagePath != null) {
                    lostFoundRepository.deleteFile(imagePath)
                }
                Log.d("LOST_FOUND_UPLOAD", "Uploading new image for item: ${currentItem.id}")
                val uploadResult = lostFoundRepository.uploadImage(currentItem.id, newImageUri)
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrNull()?.first
                    imagePath = uploadResult.getOrNull()?.second
                } else {
                    _uiState.update { it.copy(isSubmitting = false, error = "Failed to upload image") }
                    return@launch
                }
            }

            val updatedItem = currentItem.copy(
                title = title,
                description = description,
                category = category,
                type = type,
                location = location,
                contactEmail = contactEmail,
                contactPhone = contactPhone,
                imageUrl = imageUrl,
                imageStoragePath = imagePath,
                updatedAt = System.currentTimeMillis()
            )

            lostFoundRepository.updateItem(updatedItem)
                .onSuccess {
                    Log.d("LOST_FOUND_UPDATE", "Item report updated: ${currentItem.id}")
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isSubmitting = false, error = exception.message) }
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
