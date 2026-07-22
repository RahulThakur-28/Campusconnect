package com.rahul.campusconnect.presentation.placement.viewmodel

import PlacementsUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.repository.PlacementRepository
import com.rahul.campusconnect.domain.repository.UserRepository
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
                    _uiState.update {
                        it.copy(userRole = user.role)
                    }
                }
        }
    }

    fun loadPlacements() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            placementRepository.getPlacements()
                .onSuccess { placements ->

                    allPlacements = placements

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            placements = placements,
                            featuredPlacement = placements.firstOrNull(),
                            activeDrives = placements.count { placement ->
                                placement.status.equals("Active", true)
                            },
                            isEmpty = placements.isEmpty()
                        )
                    }
                }

                .onFailure { exception ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Unable to load placements.",
                            isEmpty = true
                        )
                    }
                }
        }
    }

    fun searchPlacements(query: String) {

        _uiState.update {
            it.copy(searchQuery = query)
        }

        applyFilters()
    }

    fun filterCategory(category: String) {

        _uiState.update {
            it.copy(selectedCategory = category)
        }

        applyFilters()
    }

    private fun applyFilters() {

        val state = _uiState.value

        var filtered = allPlacements

        if (state.selectedCategory != "All") {
            filtered = filtered.filter {
                it.category.equals(state.selectedCategory, true)
            }
        }

        if (state.searchQuery.isNotBlank()) {

            filtered = filtered.filter {

                it.companyName.contains(state.searchQuery, true) ||
                        it.jobRole.contains(state.searchQuery, true) ||
                        it.location.contains(state.searchQuery, true)
            }
        }

        _uiState.update {

            it.copy(
                placements = filtered,
                featuredPlacement = filtered.firstOrNull(),
                activeDrives = filtered.count { placement ->
                    placement.status.equals("Active", true)
                },
                isEmpty = filtered.isEmpty()
            )
        }
    }

    fun refresh() {
        loadPlacements()
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}