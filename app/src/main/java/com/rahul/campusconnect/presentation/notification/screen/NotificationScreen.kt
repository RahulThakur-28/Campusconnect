package com.rahul.campusconnect.presentation.notification.screen

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.model.Notification
import com.rahul.campusconnect.model.NotificationType
import com.rahul.campusconnect.presentation.notification.components.NotificationFilterChips
import com.rahul.campusconnect.presentation.notification.components.NotificationItem
import com.rahul.campusconnect.presentation.notification.state.NotificationFilter
import com.rahul.campusconnect.presentation.notification.viewmodel.NotificationViewModel
import com.rahul.campusconnect.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredNotifications = remember(uiState.notifications, uiState.selectedFilter) {
        when (uiState.selectedFilter) {
            NotificationFilter.ALL -> uiState.notifications
            NotificationFilter.UNREAD -> uiState.notifications.filter { !it.isRead }
            NotificationFilter.ANNOUNCEMENTS -> uiState.notifications.filter { it.type == NotificationType.ANNOUNCEMENT }
            NotificationFilter.EVENTS -> uiState.notifications.filter { it.type == NotificationType.EVENT }
            NotificationFilter.PLACEMENTS -> uiState.notifications.filter { it.type == NotificationType.PLACEMENT }
        }
    }

    // Grouping by time
    val groupedNotifications = remember(filteredNotifications) {
        filteredNotifications.groupBy { notification ->
            when {
                DateUtils.isToday(notification.createdAt) -> "Today"
                DateUtils.isToday(notification.createdAt + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
                else -> "Earlier"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark all as read"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NotificationFilterChips(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = viewModel::onFilterSelected
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        message = "You're all caught up\nNew campus updates will appear here."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    listOf("Today", "Yesterday", "Earlier").forEach { groupTitle ->
                        val groupItems = groupedNotifications[groupTitle]
                        if (!groupItems.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = groupTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(groupItems, key = { it.id }) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onNotificationClick = {
                                        viewModel.markAsRead(it.id)
                                        onNotificationClick(it)
                                    }
                                )
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
