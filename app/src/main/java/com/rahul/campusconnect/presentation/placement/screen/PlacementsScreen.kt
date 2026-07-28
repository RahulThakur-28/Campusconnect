package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.presentation.placement.components.*
import com.rahul.campusconnect.presentation.placement.viewmodel.PlacementsViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementsScreen(
    onPlacementClick: (String) -> Unit,
    onCreatePlacementClick: () -> Unit,
    navController: NavController,
    viewModel: PlacementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Automatic Refresh Logic
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
                val isSuccess = data.visuals.message.contains("Successfully", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = when {
                        isDelete -> MaterialTheme.colorScheme.error
                        isSuccess -> Color(0xFF10B981)
                        else -> MaterialTheme.colorScheme.inverseSurface
                    },
                    contentColor = when {
                        isDelete || isSuccess -> Color.White
                        else -> MaterialTheme.colorScheme.inverseOnSurface
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Placements",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Season ${uiState.season} • ${uiState.activeDrives} Active",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                visible = uiState.canCreatePlacement,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = onCreatePlacementClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Post Drive", fontWeight = FontWeight.Bold) }
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
                        onValueChange = viewModel::searchPlacements,
                        hint = "Search by company, role...",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // Filter & Sort Row
                item {
                    PlacementFilters(uiState = uiState, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Loading & Error States
                when {
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

                    uiState.isLoading && !uiState.isRefreshing -> {
                        items(5) {
                            PlacementCardShimmer()
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            EmptyState(
                                message = "No Drives Available",
                                description = "Check back later for new placement opportunities",
                                icon = Icons.Default.WorkHistory,
                                modifier = Modifier.fillParentMaxHeight(0.6f)
                            )
                        }
                    }

                    else -> {
                        // Featured Placement Section
                        uiState.featuredPlacement?.let { featured ->
                            if (uiState.searchQuery.isBlank() && uiState.selectedCategory == "All") {
                                item {
                                    SectionHeader(title = "Featured Drive", actionText = null)
                                    FeaturedPlacementCard(
                                        placement = featured,
                                        onClick = { onPlacementClick(featured.id) }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        item {
                            SectionHeader(title = "All Opportunities", actionText = null)
                        }

                        items(
                            items = uiState.placements,
                            key = { it.id }
                        ) { placement ->
                            PlacementCard(
                                placement = placement,
                                onClick = { onPlacementClick(placement.id) },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}
