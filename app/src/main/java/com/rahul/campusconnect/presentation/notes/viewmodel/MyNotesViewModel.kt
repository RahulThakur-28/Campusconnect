package com.rahul.campusconnect.presentation.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.NotesRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.notes.state.NotesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyNotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadMyNotes()
    }

    fun loadMyNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            userRepository.getCurrentUser().onSuccess { user ->
                notesRepository.getMyNotes(user.uid).onSuccess { notes ->
                    _uiState.update { it.copy(isLoading = false, notes = notes, filteredNotes = notes) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadMyNotes()
    }
}
