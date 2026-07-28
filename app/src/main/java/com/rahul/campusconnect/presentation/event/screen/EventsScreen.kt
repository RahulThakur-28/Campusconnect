package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        snackbarMessage?.value?.let {
            snackbarHostState.showSnackbar(it)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("snackbar_message")
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val isDelete = data.visuals.message.contains("Deleted", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isDelete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inverseSurface,
                    contentColor = if (isDelete) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campus Events",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(com.rahul.campusconnect.navigation.AppRoutes.Notifications.route) }) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
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
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = onCreateEventClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Create Event", fontWeight = FontWeight.Bold) }
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
                // Search Bar Section
                item {
                    SearchBar(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        hint = "Search events, venues .......",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // Filter Tabs
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        ScrollableTabRow(
                            selectedTabIndex = uiState.selectedTab.ordinal,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            edgePadding = 24.dp,
                            divider = {},
                            indicator = { tabPositions ->
                                if (uiState.selectedTab.ordinal < tabPositions.size) {
                                    val color = when (uiState.selectedTab) {
                                        EventTab.UPCOMING -> Color(0xFF2563EB)
                                        EventTab.ONGOING -> Color(0xFF10B981)
                                        EventTab.PAST -> Color(0xFF64748B)
                                    }
                                    TabRowDefaults.SecondaryIndicator(
                                        modifier = Modifier.fillMaxWidth().tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                                        color = color,
                                        height = 3.dp
                                    )
                                }
                            }
                        ) {
                            EventTab.entries.forEach { tab ->
                                val isSelected = uiState.selectedTab == tab
                                val selectedColor = when (tab) {
                                    EventTab.UPCOMING -> Color(0xFF2563EB)
                                    EventTab.ONGOING -> Color(0xFF10B981)
                                    EventTab.PAST -> Color(0xFF64748B)
                                }
                                
                                Tab(
                                    selected = isSelected,
                                    onClick = { viewModel.onTabSelected(tab) },
                                    text = {
                                        Text(
                                            text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    },
                                    selectedContentColor = selectedColor,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                // Category Chips
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
                                Icons.Rounded.FilterList, 
                                null, 
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
                    uiState.isLoading && !uiState.isRefreshing -> {
                        item {
                            EventLoadingShimmer()
                        }
                    }

                    uiState.error != null -> {
                        item {
                            EmptyState(
                                message = "Oops! Something went wrong",
                                description = uiState.error,
                                buttonText = "Try Again",
                                onButtonClick = viewModel::refresh,
                                modifier = Modifier.fillParentMaxHeight(0.6f)
                            )
                        }
                    }

                    uiState.filteredEvents.isEmpty() -> {
                        item {
                            val emptyMessage = when {
                                uiState.searchQuery.isNotBlank() -> "No matching events found"
                                uiState.selectedTab == EventTab.UPCOMING -> "No Upcoming Events"
                                uiState.selectedTab == EventTab.ONGOING -> "No Ongoing Events"
                                uiState.selectedTab == EventTab.PAST -> "No Past Events"
                                else -> "No events available"
                            }
                            EmptyState(
                                message = emptyMessage,
                                description = "Check back later or try changing filters",
                                modifier = Modifier.fillParentMaxHeight(0.6f)
                            )
                        }
                    }

                    else -> {
                        // Featured Section (Only for Upcoming Tab and no Search/Filter)
                        if (uiState.selectedTab == EventTab.UPCOMING && 
                            uiState.searchQuery.isBlank() && 
                            uiState.selectedCategory == "All") {
                            
                            uiState.featuredEvent?.let { featured ->
                                item {
                                    SectionHeader(
                                        title = "Editor's Pick", 
                                        actionText = null,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    FeaturedEventCard(
                                        event = featured,
                                        onClick = { onEventClick(featured.id) },
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    SectionHeader(title = "All Upcoming", actionText = null)
                                }
                            }
                        }

                        items(
                            items = uiState.filteredEvents,
                            key = { it.id }
                        ) { event ->
                            EventCard(
                                event = event,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
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
