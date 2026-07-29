package com.rahul.campusconnect.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.domain.repository.*
import com.rahul.campusconnect.presentation.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val announcementRepository: AnnouncementRepository,
    private val eventRepository: EventRepository,
    private val placementRepository: PlacementRepository,
    private val notesRepository: NotesRepository,
    private val lostFoundRepository: LostFoundRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        observeNotifications()
        loadAllData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeNotifications() {
        userRepository.currentUser
            .filterNotNull()
            .flatMapLatest { user ->
                notificationRepository.getUnreadCount(user.collegeId, user.uid)
            }
            .onEach { count ->
                _uiState.update { it.copy(notificationCount = count) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUser() {
        userRepository.currentUser
            .onEach { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            userName = it.fullName,
                            department = it.department,
                            academicYear = it.academicYear,
                            isVerified = it.verificationStatus == Constants.STATUS_VERIFIED,
                            profileImageUrl = it.profileImage,
                            role = it.role.name
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadAllData(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {
                // Fetch data in parallel
                val announcementsDeferred = async { announcementRepository.getAnnouncements() }
                val eventsDeferred = async { eventRepository.getUpcomingEvents() }
                val placementsDeferred = async { placementRepository.getPlacements() }
                val notesDeferred = async { notesRepository.getNotes() }
                val lostFoundDeferred = async { lostFoundRepository.getItems() }

                val announcementsFull = announcementsDeferred.await().getOrDefault(emptyList())
                val eventsFull = eventsDeferred.await().getOrDefault(emptyList())
                val placementsFull = placementsDeferred.await().getOrDefault(emptyList())
                val notesFull = notesDeferred.await().getOrDefault(emptyList())
                val lostFoundFull = lostFoundDeferred.await().getOrDefault(emptyList())
                    .filter { it.status == "ACTIVE" }

                _uiState.update {
                    it.copy(
                        announcements = announcementsFull.take(5),
                        announcementsCount = announcementsFull.size,
                        events = eventsFull.take(5),
                        eventsCount = eventsFull.size,
                        placements = placementsFull.take(5),
                        placementsCount = placementsFull.size,
                        notes = notesFull.take(5),
                        notesCount = notesFull.size,
                        lostFoundItems = lostFoundFull.take(5),
                        lostFoundItemsCount = lostFoundFull.size,
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                Log.d("HOME_QUERY", "Home dashboard data refreshed with counts")

            } catch (e: Exception) {
                Log.e("HOME_ERROR", "Error loading home data", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadAllData(isRefreshing = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
