package com.rahul.campusconnect.presentation.announcement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.announcement.state.AnnouncementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementUiState())
    val uiState: StateFlow<AnnouncementUiState> = _uiState.asStateFlow()

    private var allAnnouncements: List<Announcement> = emptyList()

    init {
        loadUserRole()
        loadAnnouncements()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(userRole = user.role) }
                }
        }
    }

    fun loadAnnouncements(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            announcementRepository.getAnnouncements()
                .onSuccess { announcements ->
                    allAnnouncements = announcements
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, announcements = announcements) }
                    applyFilters()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message ?: "Unable to load announcements."
                        )
                    }
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

        val filtered = allAnnouncements.filter { announcement ->
            val matchesSearch = currentState.searchQuery.isBlank() ||
                    announcement.title.contains(currentState.searchQuery, ignoreCase = true) ||
                    announcement.description.contains(currentState.searchQuery, ignoreCase = true)

            val matchesCategory = currentState.selectedCategory == "All" ||
                    announcement.category == currentState.selectedCategory

            matchesSearch && matchesCategory
        }

        _uiState.update { it.copy(filteredAnnouncements = filtered) }
    }

    fun refresh() {
        loadAnnouncements(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
