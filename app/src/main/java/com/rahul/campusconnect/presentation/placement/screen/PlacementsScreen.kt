package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkHistory
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
import com.rahul.campusconnect.presentation.placement.components.*
import com.rahul.campusconnect.presentation.placement.viewmodel.PlacementsViewModel
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.SectionHeader

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

    // 1. Automatic Refresh Logic using Navigation savedStateHandle
    val refreshSignal = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            viewModel.refresh()
            snackbarHostState.showSnackbar("Placements updated")
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Placements",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Placement Season ${uiState.season}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${uiState.activeDrives} Active Drives",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.canCreatePlacement,
                enter = fadeIn() + expandIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onCreatePlacementClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Placement")
                }
            }
        }
    ) { padding ->
        // 6. Pull To Refresh Integration
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 7. Search Bar
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::searchPlacements,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Search by company name...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )
                }

                // 8 & 9. Filter & Sort Row
                item {
                    PlacementFilters(uiState = uiState, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3 & 5. Loading & Error States
                when {
                    uiState.error != null -> {
                        item {
                            EmptyState(
                                message = uiState.error ?: "Failed to load",
                                buttonText = "Retry",
                                onButtonClick = viewModel::refresh,
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    uiState.isLoading -> {
                        items(5) {
                            PlacementCardShimmer()
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            EmptyState(
                                message = "No placements available",
                                description = "If you're a Placement Cell member you can create one.",
                                icon = Icons.Default.WorkHistory,
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    else -> {
                        // 1. Featured Placement Section
                        uiState.featuredPlacement?.let { featured ->
                            if (uiState.searchQuery.isBlank() && uiState.selectedCategory == "All") {
                                item {
                                    FeaturedPlacementCard(
                                        placement = featured,
                                        onClick = { onPlacementClick(featured.id) }
                                    )
                                }
                            }
                        }

                        item {
                            SectionHeader(title = "Ongoing Drives", actionText = null)
                        }

                        // 13. List item animations
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
