package com.rahul.campusconnect.presentation.notification.screen

import android.text.format.DateUtils
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.presentation.notification.components.NotificationFilterChips
import com.rahul.campusconnect.presentation.notification.components.NotificationItem
import com.rahul.campusconnect.presentation.notification.state.NotificationFilter
import com.rahul.campusconnect.presentation.notification.viewmodel.NotificationViewModel
import com.rahul.campusconnect.ui.components.EmptyState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filteredNotifications = remember(uiState.notifications, uiState.selectedFilter) {
        when (uiState.selectedFilter) {
            NotificationFilter.ALL -> uiState.notifications
            NotificationFilter.UNREAD -> uiState.notifications.filter { !it.isRead }
            NotificationFilter.ANNOUNCEMENTS -> uiState.notifications.filter { it.type == NotificationType.ANNOUNCEMENT }
            NotificationFilter.EVENTS -> uiState.notifications.filter { it.type == NotificationType.EVENT }
            NotificationFilter.PLACEMENTS -> uiState.notifications.filter { it.type == NotificationType.PLACEMENT }
        }
    }

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
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val isSuccess = !data.visuals.message.lowercase().contains("error")
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Notifications", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.notifications.any { !it.isRead }) {
                        TextButton(
                            onClick = { viewModel.markAllAsRead() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Rounded.DoneAll, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Mark all read", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NotificationFilterChips(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::onFilterSelected
                )

                if (uiState.isLoading && !uiState.isRefreshing) {
                    NotificationShimmer()
                } else if (filteredNotifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            message = "No Notifications Yet",
                            description = "You're all caught up.\nNew campus updates will appear here."
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        listOf("Today", "Yesterday", "Earlier").forEach { groupTitle ->
                            val groupItems = groupedNotifications[groupTitle]
                            if (!groupItems.isNullOrEmpty()) {
                                item {
                                    Text(
                                        text = groupTitle.uppercase(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                items(groupItems, key = { it.id }) { notification ->
                                    SwipeToDismissNotification(
                                        notification = notification,
                                        onDismiss = {
                                            viewModel.deleteNotification(notification.id)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Notification deleted",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.markAsRead(notification.id)
                                            onNotificationClick(notification)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissNotification(
    notification: Notification,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = MaterialTheme.colorScheme.errorContainer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(color, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        NotificationItem(
            notification = notification,
            onNotificationClick = { onClick() },
            onDelete = onDismiss,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun NotificationShimmer() {
    Column(modifier = Modifier.padding(16.dp)) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            )
        }
    }
}
