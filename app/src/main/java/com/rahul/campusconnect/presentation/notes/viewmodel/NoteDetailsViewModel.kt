package com.rahul.campusconnect.presentation.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.NotesRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.notes.state.NoteDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailsUiState())
    val uiState: StateFlow<NoteDetailsUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            userRepository.getCurrentUser().onSuccess { user ->
                _uiState.update { it.copy(userRole = user.role, currentUserId = user.uid) }
            }

            notesRepository.getNoteById(noteId)
                .onSuccess { note ->
                    if (note == null) {
                        _uiState.update { it.copy(isLoading = false, error = "Note not found") }
                    } else {
                        _uiState.update { it.copy(note = note, isLoading = false) }
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    fun deleteNote() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            
            // Delete file from storage first (Optional: Soft delete might keep files, but usually best to cleanup if permitted)
            // For production soft delete, we just mark as deleted in Firestore.
            
            notesRepository.deleteNote(note.id)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isDeleting = false, error = exception.message) }
                }
        }
    }

    fun incrementDownloadCount() {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            notesRepository.incrementDownloadCount(noteId)
        }
    }

    fun resetDeleteState() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
