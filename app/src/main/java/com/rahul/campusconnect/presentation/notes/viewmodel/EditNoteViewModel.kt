package com.rahul.campusconnect.presentation.notes.viewmodel

import android.net.Uri
import android.util.Log
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
            _uiState.update { it.copy(isSubmitting = true, error = null, isSuccess = false) }
            
            val oldFilePath = currentNote.storagePath
            val oldThumbnailPath = currentNote.thumbnailStoragePath

            var fileUrl = currentNote.fileUrl
            var storagePath = currentNote.storagePath
            var fileType = currentNote.fileType
            var fileExtension = currentNote.fileExtension
            var fileSize = currentNote.fileSize
            var thumbnailUrl = currentNote.thumbnailUrl
            var thumbnailPath = currentNote.thumbnailStoragePath

            try {
                // ====================================================
                // THUMBNAIL HANDLING
                // ====================================================
                
                if (removeThumbnail && newThumbnailUri == null) {
                    thumbnailUrl = null
                    thumbnailPath = null
                }

                if (newThumbnailUri != null) {
                    Log.d("NOTES_UPLOAD", "Uploading new thumbnail for note: ${currentNote.id}")
                    val thumbResult = notesRepository.uploadThumbnail(currentNote.collegeId, currentNote.id, newThumbnailUri)
                    if (thumbResult.isSuccess) {
                        thumbnailUrl = thumbResult.getOrNull()?.first
                        thumbnailPath = thumbResult.getOrNull()?.second
                    } else {
                        throw thumbResult.exceptionOrNull() ?: Exception("Thumbnail upload failed")
                    }
                }

                // ====================================================
                // FILE HANDLING
                // ====================================================

                if (newFileUri != null && newFileExtension != null && newFileSize != null) {
                    Log.d("NOTES_UPLOAD", "Uploading new file for note: ${currentNote.id}")
                    val uploadResult = notesRepository.uploadAttachment(currentNote.collegeId, currentNote.id, newFileUri, newFileExtension)
                    if (uploadResult.isSuccess) {
                        val (newUrl, newPath) = uploadResult.getOrThrow()
                        fileUrl = newUrl
                        storagePath = newPath
                        fileType = newFileExtension.uppercase()
                        fileExtension = newFileExtension
                        fileSize = newFileSize
                    } else {
                        throw uploadResult.exceptionOrNull() ?: Exception("File upload failed")
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

                // ====================================================
                // FIRESTORE UPDATE
                // ====================================================

                notesRepository.updateNote(updatedNote).onSuccess {
                    Log.d("NOTES_UPDATE", "Note updated successfully: ${currentNote.id}")

                    // Cleanup old thumbnail
                    if (thumbnailPath != oldThumbnailPath && oldThumbnailPath != null) {
                        notesRepository.deleteFile(oldThumbnailPath)
                    }

                    // Cleanup old file
                    if (storagePath != oldFilePath) {
                        notesRepository.deleteFile(oldFilePath)
                    }

                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true, note = updatedNote) }
                }.onFailure { e ->
                    // Rollback newly uploaded files if Firestore fails
                    if (thumbnailPath != oldThumbnailPath && thumbnailPath != null) {
                        notesRepository.deleteFile(thumbnailPath)
                    }
                    if (storagePath != oldFilePath) {
                        notesRepository.deleteFile(storagePath)
                    }
                    throw e
                }

            } catch (e: Exception) {
                Log.e("NOTES_UPDATE", "Error updating note", e)
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to update note") }
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
