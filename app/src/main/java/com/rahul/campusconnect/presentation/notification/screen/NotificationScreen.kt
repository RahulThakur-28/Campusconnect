package com.rahul.campusconnect.presentation.notification.screen

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.Delete
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
import com.rahul.campusconnect.presentation.notification.components.NotificationFilterChips
import com.rahul.campusconnect.presentation.notification.components.NotificationItem
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
    val filteredNotifications by viewModel.filteredNotifications.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                val isError = data.visuals.message.lowercase().contains("error")
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF10B981),
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
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (filteredNotifications.any { !it.isRead }) {
                        IconButton(
                            onClick = { viewModel.markAllAsRead() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = "Mark all read",
                                tint = MaterialTheme.colorScheme.primary
                            )
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
                            message = "No Notifications",
                            description = "You're all caught up! New updates from your campus will appear here."
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
                                item(key = groupTitle) {
                                    Text(
                                        text = groupTitle.uppercase(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            letterSpacing = 1.2.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
                                    )
                                }
                                items(groupItems, key = { it.id }) { notification ->
                                    SwipeToDismissNotification(
                                        notification = notification,
                                        onDismiss = {
                                            viewModel.deleteNotification(notification.id)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Notification removed",
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        NotificationItem(
            notification = notification,
            onNotificationClick = { onClick() },
            onDelete = onDismiss
        )
    }
}

@Composable
fun NotificationShimmer() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(vertical = 6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            )
        }
    }
}
