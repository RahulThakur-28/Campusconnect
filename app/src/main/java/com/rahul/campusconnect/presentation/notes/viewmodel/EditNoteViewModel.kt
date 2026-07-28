package com.rahul.campusconnect.presentation.notes.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.repository.NotesRepository
import com.rahul.campusconnect.presentation.notes.state.EditNoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditNoteUiState())
    val uiState: StateFlow<EditNoteUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            notesRepository.getNoteById(noteId)
                .onSuccess { note ->
                    if (note == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Note not found") }
                    } else {
                        _uiState.update { it.copy(note = note, isLoading = false) }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateNote(
        title: String,
        description: String,
        subject: String,
        semester: String,
        branch: String,
        tags: List<String>,
        newFileUri: Uri? = null,
        newFileExtension: String? = null,
        newFileSize: String? = null,
        newThumbnailUri: Uri? = null,
        removeThumbnail: Boolean = false
    ) {
        val currentNote = _uiState.value.note ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            
            var fileUrl = currentNote.fileUrl
            var storagePath = currentNote.storagePath
            var fileType = currentNote.fileType
            var fileExtension = currentNote.fileExtension
            var fileSize = currentNote.fileSize
            var thumbnailUrl = currentNote.thumbnailUrl
            var thumbnailPath = currentNote.thumbnailStoragePath

            // Handle Thumbnail Removal
            if (removeThumbnail && thumbnailPath != null) {
                notesRepository.deleteFile(thumbnailPath)
                thumbnailUrl = null
                thumbnailPath = null
            }

            // Handle New Thumbnail Upload
            if (newThumbnailUri != null) {
                if (thumbnailPath != null) {
                    notesRepository.deleteFile(thumbnailPath)
                }
                val thumbResult = notesRepository.uploadThumbnail(currentNote.id, newThumbnailUri)
                if (thumbResult.isSuccess) {
                    thumbnailUrl = thumbResult.getOrNull()?.first
                    thumbnailPath = thumbResult.getOrNull()?.second
                }
            }

            if (newFileUri != null && newFileExtension != null && newFileSize != null) {
                // Delete old file
                notesRepository.deleteFile(storagePath)
                
                // Upload new file
                val uploadResult = notesRepository.uploadAttachment(currentNote.id, newFileUri, newFileExtension)
                if (uploadResult.isSuccess) {
                    val (newUrl, newPath) = uploadResult.getOrThrow()
                    fileUrl = newUrl
                    storagePath = newPath
                    fileType = newFileExtension.uppercase()
                    fileExtension = newFileExtension
                    fileSize = newFileSize
                } else {
                    _uiState.update { it.copy(isSubmitting = false, error = "Failed to upload new file") }
                    return@launch
                }
            }

            val updatedNote = currentNote.copy(
                title = title,
                description = description,
                subject = subject,
                semester = semester,
                branch = branch,
                tags = tags,
                fileUrl = fileUrl,
                storagePath = storagePath,
                thumbnailUrl = thumbnailUrl,
                thumbnailStoragePath = thumbnailPath,
                fileType = fileType,
                fileExtension = fileExtension,
                fileSize = fileSize,
                updatedAt = System.currentTimeMillis()
            )

            notesRepository.updateNote(updatedNote).onSuccess {
                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmitting = false, error = e.message) }
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
