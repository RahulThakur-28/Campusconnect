package com.rahul.campusconnect.presentation.lostfound.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.FilterList
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
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundTab
import com.rahul.campusconnect.presentation.lostfound.viewmodel.LostFoundViewModel
import com.rahul.campusconnect.ui.components.*
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundScreen(
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onReportClick: () -> Unit,
    navController: NavController,
    viewModel: LostFoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe signals from Navigation BackStack
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
        snackbarMessage?.value?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("snackbar_message")
        }
    }

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val msg = data.visuals.message.lowercase()
                val containerColor = when {
                    msg.contains("success") || msg.contains("successfully") || msg.contains("resolved") -> SuccessGreen
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
                        text = "Lost & Found", 
                        style = MaterialTheme.typography.headlineSmall.copy(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Report Item", fontWeight = FontWeight.Bold) }
            )
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
                        hint = "Search items, locations...",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // Tabs (LOST, FOUND, RESOLVED)
                item {
                    val tabColor = when(uiState.selectedTab) {
                        LostFoundTab.LOST -> Color(0xFFEF4444)
                        LostFoundTab.FOUND -> Color(0xFF3B82F6)
                        LostFoundTab.RESOLVED -> Color(0xFF10B981)
                    }

                    ScrollableTabRow(
                        selectedTabIndex = uiState.selectedTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.background,
                        edgePadding = 24.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (uiState.selectedTab.ordinal < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                                    color = tabColor
                                )
                            }
                        }
                    ) {
                        LostFoundTab.entries.forEach { tab ->
                            val isSelected = uiState.selectedTab == tab
                            val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            val selectedColor = when(tab) {
                                LostFoundTab.LOST -> Color(0xFFEF4444)
                                LostFoundTab.FOUND -> Color(0xFF3B82F6)
                                LostFoundTab.RESOLVED -> Color(0xFF10B981)
                            }
                            
                            Tab(
                                selected = isSelected,
                                onClick = { viewModel.onTabSelected(tab) },
                                text = { 
                                    Text(
                                        text = tab.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) selectedColor else unselectedColor
                                    ) 
                                }
                            )
                        }
                    }
                }

                // Filter & Sort Row
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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
                        }

                        // Sort Chip
                        item {
                            Box {
                                FilterChip(
                                    selected = uiState.selectedSort != "Newest First",
                                    onClick = { showSortMenu = true },
                                    label = { Text(uiState.selectedSort) },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null, Modifier.size(18.dp)) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    uiState.sortOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                viewModel.onSortSelected(option)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Categories
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
                            LostFoundLoadingShimmer()
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            val emptyMessage = when {
                                uiState.searchQuery.isNotBlank() -> "No results for \"${uiState.searchQuery}\""
                                uiState.selectedTab == LostFoundTab.LOST -> "No Lost Items reported"
                                uiState.selectedTab == LostFoundTab.FOUND -> "No Found Items reported"
                                else -> "No Resolved Items"
                            }
                            
                            EmptyState(
                                message = emptyMessage,
                                description = "Keep checking back or report an item yourself.",
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    else -> {
                        items(
                            items = uiState.filteredItems,
                            key = { it.id }
                        ) { item ->
                            LostFoundCard(
                                item = item,
                                onClick = { onItemClick(item.id) },
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}
