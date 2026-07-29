package com.rahul.campusconnect.presentation.announcement.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.viewmodel.AnnouncementViewModel
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.ui.components.*
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    onAnnouncementClick: (String) -> Unit,
    onCreateAnnouncementClick: () -> Unit,
    navController: NavController,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe refresh signal from Navigation BackStack
    val refreshSignal = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    val snackbarMessage = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("snackbar_message")
        ?.observeAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            viewModel.refresh()
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh")
        }
    }

    LaunchedEffect(snackbarMessage?.value) {
        snackbarMessage?.value?.let { message ->
            snackbarHostState.showSnackbar(message)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("snackbar_message")
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val msg = data.visuals.message.lowercase()
                val containerColor = when {
                    msg.contains("success") || msg.contains("successfully") -> SuccessGreen
                    msg.contains("warning") -> Color(0xFFF2994A) // Orange
                    msg.contains("info") -> Color(0xFF2D9CDB) // Blue
                    else -> MaterialTheme.colorScheme.error
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = containerColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Announcements", 
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.canPublishAnnouncement,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = onCreateAnnouncementClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Publish", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Search Bar
                item {
                    SearchBar(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        hint = "Search announcements, subjects...",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // Filter Chips
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        items(uiState.categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = uiState.selectedCategory == category,
                                onClick = { viewModel.onCategorySelected(category) }
                            )
                        }
                    }
                }

                when {
                    uiState.error != null -> {
                        item {
                            EmptyState(
                                message = "Oops! Something went wrong",
                                description = uiState.error,
                                buttonText = "Try Again",
                                onButtonClick = viewModel::refresh,
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }
                    
                    uiState.isLoading && !uiState.isRefreshing -> {
                        item {
                            AnnouncementLoadingShimmer()
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            val emptyMessage = if (uiState.searchQuery.isNotBlank()) {
                                "No results for \"${uiState.searchQuery}\""
                            } else if (uiState.selectedCategory != "All") {
                                "No announcements in ${uiState.selectedCategory}"
                            } else {
                                "No Announcements Yet"
                            }
                            
                            EmptyState(
                                message = emptyMessage,
                                description = "Check back later for new updates from your campus.",
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    else -> {
                        items(
                            items = uiState.filteredAnnouncements,
                            key = { it.id }
                        ) { announcement ->
                            AnnouncementCard(
                                announcement = announcement,
                                cardWidth = Dp.Unspecified,
                                onCardClick = { onAnnouncementClick(announcement.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                                    .animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}
