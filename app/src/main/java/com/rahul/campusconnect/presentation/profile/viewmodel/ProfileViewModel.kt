package com.rahul.campusconnect.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.*
import com.rahul.campusconnect.presentation.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate delay

            val dummyUser = UserProfile(
                id = "u1",
                name = "Rahul Thakur",
                email = "rahul.cse.22@college.edu",
                role = UserRole.VERIFIED_STUDENT,
                department = "CSE",
                semester = "4th",
                studentId = "2022CSE0101",
                isVerified = true,
                stats = ProfileStats(
                    notesUploaded = 12,
                    eventsJoined = 8,
                    placementApplications = 5,
                    lostFoundReports = 3
                )
            )

            val dummyNotes = listOf(
                Note(id = "1", title = "DSA Unit 1", department = "CSE", semester = "4th", uploadedBy = "Rahul Thakur", downloads = 120),
                Note(id = "2", title = "OS Memory Management", department = "CSE", semester = "4th", uploadedBy = "Rahul Thakur", downloads = 85)
            )

            val dummyEvents = listOf(
                Event(id = "1", title = "TechQuest 2024", category = "Academic", date = "24 Oct", venue = "Auditorium A")
            )

            val dummyLostFound = listOf(
                LostFoundItem(id = "1", title = "Blue Bottle", location = "Library", reportedDate = "28 Jun", status = "Lost")
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = dummyUser,
                    myNotes = dummyNotes,
                    myEvents = dummyEvents,
                    myLostFoundItems = dummyLostFound
                )
            }
        }
    }

    fun updateProfile(name: String, dept: String, semester: String, phone: String, bio: String) {
        // In real app, call repository to update
        _uiState.update { 
            it.copy(
                user = it.user.copy(
                    name = name,
                    department = dept,
                    semester = semester,
                    phoneNumber = phone,
                    bio = bio
                )
            )
        }
    }
}
