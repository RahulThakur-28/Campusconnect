package com.rahul.campusconnect.presentation.notes.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.repository.NotesRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.notes.state.CreateNoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNoteUiState())
    val uiState: StateFlow<CreateNoteUiState> = _uiState.asStateFlow()

    fun createNote(
        title: String,
        description: String,
        subject: String,
        semester: String,
        branch: String,
        fileUri: Uri,
        fileExtension: String,
        fileSize: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            userRepository.getCurrentUser().onSuccess { user ->
                val noteId = UUID.randomUUID().toString()

                Log.d("NOTES_UPLOAD", "Starting attachment upload for note: $noteId")
                val uploadResult = notesRepository.uploadAttachment(noteId, fileUri, fileExtension)
                
                if (uploadResult.isSuccess) {
                    val (fileUrl, storagePath) = uploadResult.getOrThrow()
                    
                    val note = Note(
                        id = noteId,
                        title = title,
                        description = description,
                        subject = subject,
                        semester = semester,
                        branch = branch,
                        uploadedBy = user.uid,
                        uploadedByName = user.fullName,
                        uploadedByRole = user.role.name,
                        collegeId = user.collegeId,
                        fileUrl = fileUrl,
                        storagePath = storagePath,
                        fileType = fileExtension.uppercase(),
                        fileExtension = fileExtension,
                        fileSize = fileSize,
                        tags = tags,
                        isVerified = user.verificationStatus == "VERIFIED",
                        createdAt = System.currentTimeMillis()
                    )

                    notesRepository.createNote(note).onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to save note details") }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to upload file") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to get user info") }
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
