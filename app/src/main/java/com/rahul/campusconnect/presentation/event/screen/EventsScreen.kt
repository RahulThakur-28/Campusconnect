package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.presentation.event.components.*
import com.rahul.campusconnect.presentation.event.state.EventTab
import com.rahul.campusconnect.presentation.event.viewmodel.EventsViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    navController: NavController,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatic Refresh Logic
    val refreshSignal = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            viewModel.refresh()
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Events",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(com.rahul.campusconnect.navigation.AppRoutes.Notifications.route) }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.canCreateEvent,
                enter = fadeIn() + expandIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onCreateEventClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Event")
                }
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
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Search Bar
                item {
                    SearchTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = "Search events, venues...",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Tab Row (Upcoming, Ongoing, Past)
                item {
                    PrimaryTabRow(
                        selectedTabIndex = uiState.selectedTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.background,
                        divider = {}
                    ) {
                        EventTab.entries.forEach { tab ->
                            Tab(
                                selected = uiState.selectedTab == tab,
                                onClick = { viewModel.onTabSelected(tab) },
                                text = {
                                    Text(
                                        text = tab.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }

                // Category Row
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                    uiState.isLoading && !uiState.isRefreshing -> {
                        item {
                            Box(modifier = Modifier.fillParentMaxHeight(0.7f), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    uiState.error != null -> {
                        item {
                            EmptyState(
                                message = uiState.error ?: "An error occurred",
                                buttonText = "Retry",
                                onButtonClick = viewModel::refresh,
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            EmptyState(
                                message = "No events found",
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    else -> {
                        // Featured Section (Only for Upcoming Tab and no Search)
                        if (uiState.selectedTab == EventTab.UPCOMING && uiState.searchQuery.isBlank() && uiState.selectedCategory == "All") {
                            uiState.featuredEvent?.let { featured ->
                                item {
                                    SectionHeader(title = "Featured Event", actionText = null)
                                    FeaturedEventCard(
                                        event = featured,
                                        onClick = { onEventClick(featured.id) }
                                    )
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = when(uiState.selectedTab) {
                                    EventTab.UPCOMING -> "Upcoming Events"
                                    EventTab.ONGOING -> "Ongoing Events"
                                    EventTab.PAST -> "Past Events"
                                },
                                actionText = null
                            )
                        }

                        items(
                            items = uiState.filteredEvents,
                            key = { it.id }
                        ) { event ->
                            EventCard(
                                event = event,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .animateItem(),
                                cardStyle = EventCardStyle.Large,
                                onClick = { onEventClick(event.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
