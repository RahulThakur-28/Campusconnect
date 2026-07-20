package com.rahul.campusconnect.presentation.placement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.presentation.placement.state.PlacementsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacementsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PlacementsUiState())
    val uiState: StateFlow<PlacementsUiState> = _uiState.asStateFlow()

    init {
        loadPlacements()
    }

    fun loadPlacements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulating API delay
            delay(1000)

            val fakePlacements = listOf(
                Placement(
                    id = "p1",
                    companyName = "Google",
                    role = "Software Engineer",
                    packageAmount = "45 LPA",
                    location = "Bangalore",
                    jobType = "Full-time",
                    openings = 10,
                    deadline = "30 Oct 2024",
                    applyLink = "https://google.com/careers",
                    logoUrl = "",
                    eligibility = "7.5 CGPA+",
                    category = "IT",
                    description = "Join Google's dynamic engineering team to build scalable systems.",
                    requiredSkills = listOf("Kotlin", "Java", "System Design"),
                    applicationProcess = "Online Assessment -> Technical Interview -> HR Interview"
                ),
                Placement(
                    id = "p2",
                    companyName = "Goldman Sachs",
                    role = "Analyst",
                    packageAmount = "32 LPA",
                    location = "Hyderabad",
                    jobType = "Full-time",
                    openings = 5,
                    deadline = "15 Nov 2024",
                    applyLink = "https://goldmansachs.com",
                    logoUrl = "",
                    eligibility = "8.0 CGPA+",
                    category = "Finance",
                    description = "Work in the heart of financial technology and analysis.",
                    requiredSkills = listOf("Data Analysis", "Python", "SQL"),
                    applicationProcess = "Resume Screening -> Aptitude Test -> Technical Interview"
                )
            )

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    placements = fakePlacements,
                    featuredPlacement = fakePlacements.firstOrNull()
                ) 
            }
        }
    }

    fun searchPlacements(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun filterCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
