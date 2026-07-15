package com.rahul.campusconnect.presentation.announcement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.Announcement
import com.rahul.campusconnect.model.UserRole
import com.rahul.campusconnect.presentation.announcement.state.AnnouncementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementUiState())
    val uiState: StateFlow<AnnouncementUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
        // Simulate getting user role
        _uiState.update { it.copy(userRole = UserRole.VERIFIED_TEACHER) }
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network delay

            val dummyData = listOf(
                Announcement(
                    id = "1",
                    title = "End Semester Examination Schedule Out",
                    description = "The end semester examinations for all departments will commence from 15th December. Students are advised to check the detailed schedule on the official notice board or download the attached PDF.",
                    category = "Exam",
                    postedBy = "Admin",
                    isVerified = true,
                    timestamp = "2 hours ago",
                    hasAttachment = true
                ),
                Announcement(
                    id = "2",
                    title = "Winter Holiday Announcement",
                    description = "The college will remain closed from 24th December to 2nd January for winter break. Classes will resume from 3rd January. Merry Christmas and a Happy New Year to all students and staff.",
                    category = "Holiday",
                    postedBy = "Prof. Mehra",
                    isVerified = true,
                    timestamp = "5 hours ago",
                    imageUrl = "https://images.unsplash.com/photo-1544006659-f0b21f04cb1d"
                ),
                Announcement(
                    id = "3",
                    title = "Hiring: Google Summer Internships 2025",
                    description = "Google is looking for talented software engineering interns for the Summer 2025 program. Interested students from CSE/IT departments can apply through the placement cell.",
                    category = "Placement",
                    postedBy = "Placement Cell",
                    isVerified = true,
                    timestamp = "1 day ago",
                    hasAttachment = false
                )
            )

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    announcements = dummyData,
                    filteredAnnouncements = dummyData
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val filtered = currentState.announcements.filter { announcement ->
            val matchesSearch = announcement.title.contains(currentState.searchQuery, ignoreCase = true) ||
                    announcement.description.contains(currentState.searchQuery, ignoreCase = true)
            val matchesCategory = currentState.selectedCategory == "All" || announcement.category == currentState.selectedCategory
            
            matchesSearch && matchesCategory
        }
        _uiState.update { it.copy(filteredAnnouncements = filtered) }
    }
}
