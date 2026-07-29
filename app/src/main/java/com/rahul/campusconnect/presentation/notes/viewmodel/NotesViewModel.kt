package com.rahul.campusconnect.presentation.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Note
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
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var allNotes: List<Note> = emptyList()

    init {
        loadUserRole()
        loadNotes()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(userRole = user.role) }
                }
        }
    }

    fun loadNotes(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            notesRepository.getNotes()
                .onSuccess { notes ->
                    allNotes = notes
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, notes = notes) }
                    applyFilters()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message ?: "Unable to load notes."
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun setFilters(
        semester: String? = null,
        branch: String? = null,
        fileType: String? = null,
        sort: String? = null
    ) {
        _uiState.update {
            it.copy(
                selectedSemester = semester ?: it.selectedSemester,
                selectedBranch = branch ?: it.selectedBranch,
                selectedFileType = fileType ?: it.selectedFileType,
                selectedSort = sort ?: it.selectedSort
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = allNotes

        // Search
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(state.searchQuery, ignoreCase = true) ||
                it.subject.contains(state.searchQuery, ignoreCase = true) ||
                it.description.contains(state.searchQuery, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(state.searchQuery, ignoreCase = true) }
            }
        }

        // Semester Filter
        if (state.selectedSemester != "All") {
            filtered = filtered.filter { it.semester == state.selectedSemester }
        }

        // Branch Filter
        if (state.selectedBranch != "All") {
            filtered = filtered.filter { it.branch == state.selectedBranch }
        }

        // File Type Filter
        if (state.selectedFileType != "All") {
            filtered = filtered.filter { it.fileType == state.selectedFileType }
        }

        // Sorting
        filtered = when (state.selectedSort) {
            "Oldest First" -> filtered.sortedBy { it.createdAt }
            "Most Downloaded" -> filtered.sortedByDescending { it.downloadCount }
            "Alphabetical" -> filtered.sortedBy { it.title }
            else -> filtered.sortedByDescending { it.createdAt } // Newest First
        }

        _uiState.update { it.copy(filteredNotes = filtered) }
    }

    fun refresh() {
        loadNotes(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }


}
