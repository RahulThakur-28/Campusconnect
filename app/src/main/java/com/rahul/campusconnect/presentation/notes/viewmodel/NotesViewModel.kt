package com.rahul.campusconnect.presentation.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.presentation.notes.state.NotesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
        // Simulate getting user role
        _uiState.update { it.copy(userRole = UserRole.ADMIN) }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network delay

            val dummyNotes = listOf(
                Note(
                    id = "1",
                    title = "Data Structures & Algorithms",
                    subject = "Computer Science",
                    department = "CSE",
                    semester = "4th",
                    uploadedBy = "Prof. Sharma",
                    downloads = 1250,
                    pages = 45,
                    isVerified = true,
                    description = "Comprehensive notes on DSA including Trees, Graphs, and DP."
                ),
                Note(
                    id = "2",
                    title = "Operating Systems",
                    subject = "Information Technology",
                    department = "IT",
                    semester = "5th",
                    uploadedBy = "Rahul Thakur",
                    downloads = 850,
                    pages = 32,
                    isVerified = false,
                    description = "Detailed notes on Process Management and Memory Allocation."
                ),
                Note(
                    id = "3",
                    title = "Database Management Systems",
                    subject = "Computer Science",
                    department = "CSE",
                    semester = "4th",
                    uploadedBy = "Amit Kumar",
                    downloads = 2100,
                    pages = 58,
                    isVerified = true,
                    description = "SQL, Normalization, and Transaction Management explained."
                )
            )

            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    notes = dummyNotes,
                    filteredNotes = dummyNotes
                ) 
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
        applyFilters()
    }

    fun onSemesterSelected(semester: String) {
        _uiState.update { it.copy(selectedSemester = semester) }
        applyFilters()
    }

    fun onDepartmentSelected(department: String) {
        _uiState.update { it.copy(selectedDepartment = department) }
        applyFilters()
    }

    private fun applyFilters() {

        val state = _uiState.value

        val filtered = state.notes.filter { note ->

            val matchesSearch =
                state.searchQuery.isBlank() ||

                        note.title.contains(state.searchQuery, true) ||

                        note.subject.contains(state.searchQuery, true) ||

                        note.department.contains(state.searchQuery, true)

            val matchesSemester =
                state.selectedSemester == "All" ||
                        note.semester == state.selectedSemester

            val matchesDepartment =
                state.selectedDepartment == "All" ||
                        note.department == state.selectedDepartment

            matchesSearch &&
                    matchesSemester &&
                    matchesDepartment
        }

        _uiState.update {
            it.copy(filteredNotes = filtered)
        }
    }
}
