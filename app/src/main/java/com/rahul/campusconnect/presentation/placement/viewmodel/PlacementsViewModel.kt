package com.rahul.campusconnect.presentation.placement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.repository.PlacementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.placement.state.PlacementsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacementsViewModel @Inject constructor(
    private val placementRepository: PlacementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacementsUiState())
    val uiState: StateFlow<PlacementsUiState> = _uiState.asStateFlow()

    private var allPlacements: List<Placement> = emptyList()

    init {
        loadUserRole()
        loadPlacements()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(userRole = user.role) }
                }
        }
    }

    fun loadPlacements(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) _uiState.update { it.copy(isRefreshing = true) }
            else _uiState.update { it.copy(isLoading = true, error = null) }

            placementRepository.getPlacements()
                .onSuccess { placements ->
                    allPlacements = placements
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    applyFilters()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message ?: "Unable to load placements."
                        )
                    }
                }
        }
    }

    fun searchPlacements(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun setFilters(
        category: String? = null,
        jobType: String? = null,
        location: String? = null,
        sort: String? = null
    ) {
        _uiState.update {
            it.copy(
                selectedCategory = category ?: it.selectedCategory,
                selectedJobType = jobType ?: it.selectedJobType,
                selectedLocation = location ?: it.selectedLocation,
                selectedSort = sort ?: it.selectedSort
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = allPlacements

        // Category Filter
        if (state.selectedCategory != "All") {
            filtered = filtered.filter { it.category.equals(state.selectedCategory, true) }
        }

        // Job Type Filter
        if (state.selectedJobType != "All") {
            filtered = filtered.filter { it.jobType.equals(state.selectedJobType, true) }
        }

        // Location Filter
        if (state.selectedLocation != "All") {
            filtered = filtered.filter { it.mode.equals(state.selectedLocation, true) }
        }

        // Search Filter
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.companyName.contains(state.searchQuery, true) ||
                it.jobRole.contains(state.searchQuery, true) ||
                it.location.contains(state.searchQuery, true)
            }
        }

        // Sorting
        filtered = when (state.selectedSort) {
            "Oldest" -> filtered.sortedBy { it.postedAt }
            "Highest Package" -> filtered.sortedByDescending { it.packageLpa }
            "Deadline" -> filtered.sortedBy { it.deadline }
            else -> filtered.sortedByDescending { it.postedAt } // Newest
        }

        _uiState.update {
            it.copy(
                placements = filtered,
                featuredPlacement = filtered.firstOrNull(),
                activeDrives = filtered.count { p -> p.status.equals("Active", true) }
            )
        }
    }

    fun refresh() {
        loadPlacements(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
