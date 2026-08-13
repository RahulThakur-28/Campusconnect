package com.rahul.campusconnect.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.domain.repository.NotificationRepository
import com.rahul.campusconnect.presentation.notification.state.NotificationFilter
import com.rahul.campusconnect.presentation.notification.state.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredNotifications = combine(_notifications, _uiState.map { it.selectedFilter }.distinctUntilChanged()) { notifications, filter ->
        when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.ANNOUNCEMENTS -> notifications.filter { it.type == NotificationType.ANNOUNCEMENT }
            NotificationFilter.EVENTS -> notifications.filter { it.type == NotificationType.EVENT }
            NotificationFilter.PLACEMENTS -> notifications.filter { it.type == NotificationType.PLACEMENT }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeNotifications()
        observeUnreadCount()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeNotifications() {
        sessionManager.currentUser
            .filterNotNull()
            .flatMapLatest { user ->
                repository.getNotifications(user.collegeId, user.uid)
            }
            .onEach { notifications ->
                _notifications.value = notifications
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUnreadCount() {
        sessionManager.currentUser
            .filterNotNull()
            .flatMapLatest { user ->
                repository.getUnreadCount(user.collegeId, user.uid)
            }
            .onEach { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: NotificationFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun markAsRead(notificationId: String) {
        val user = sessionManager.getCurrentUser() ?: return
        viewModelScope.launch {
            repository.markAsRead(user.collegeId, notificationId)
        }
    }

    fun markAllAsRead() {
        val user = sessionManager.getCurrentUser() ?: return
        viewModelScope.launch {
            repository.markAllAsRead(user.collegeId, user.uid)
        }
    }

    fun deleteNotification(id: String) {
        val user = sessionManager.getCurrentUser() ?: return
        viewModelScope.launch {
            repository.deleteNotification(user.collegeId, id)
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
    }
}
