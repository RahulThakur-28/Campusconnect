package com.rahul.campusconnect.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.Notification
import com.rahul.campusconnect.model.NotificationType
import com.rahul.campusconnect.presentation.notification.state.NotificationFilter
import com.rahul.campusconnect.presentation.notification.state.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate delay

            val dummyNotifications = listOf(
                Notification(
                    id = "1",
                    title = "New Academic Announcement",
                    message = "End Semester Examination schedule has been published.",
                    type = NotificationType.ANNOUNCEMENT,
                    referenceId = "ann1",
                    isRead = false,
                    createdAt = System.currentTimeMillis() - 600000 // 10 min ago
                ),
                Notification(
                    id = "2",
                    title = "New Event Added",
                    message = "Android Development Workshop is happening this Friday.",
                    type = NotificationType.EVENT,
                    referenceId = "ev1",
                    isRead = false,
                    createdAt = System.currentTimeMillis() - 3600000 // 1 hour ago
                ),
                Notification(
                    id = "3",
                    title = "Placement Drive Update",
                    message = "Google Software Engineering Internship applications are now open.",
                    type = NotificationType.PLACEMENT,
                    referenceId = "pl1",
                    isRead = true,
                    createdAt = System.currentTimeMillis() - 86400000 // 1 day ago
                ),
                Notification(
                    id = "4",
                    title = "Possible Match Found",
                    message = "An item matching your lost item report has been found.",
                    type = NotificationType.LOST_FOUND,
                    referenceId = "lf1",
                    isRead = false,
                    createdAt = System.currentTimeMillis() - 172800000 // 2 days ago
                ),
                Notification(
                    id = "5",
                    title = "New Notes Uploaded",
                    message = "Operating Systems Unit 4 notes have been uploaded.",
                    type = NotificationType.NOTE,
                    referenceId = "nt1",
                    isRead = true,
                    createdAt = System.currentTimeMillis() - 259200000 // 3 days ago
                )
            )

            _uiState.update { it.copy(notifications = dummyNotifications, isLoading = false) }
        }
    }

    fun onFilterSelected(filter: NotificationFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun markAsRead(notificationId: String) {
        _uiState.update { state ->
            val updatedList = state.notifications.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
            state.copy(notifications = updatedList)
        }
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            val updatedList = state.notifications.map { it.copy(isRead = true) }
            state.copy(notifications = updatedList)
        }
    }
}
