package com.rahul.campusconnect.presentation.lostfound.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // Observe refresh signal from Navigation BackStack
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

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onReportClick) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Report Item")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onReportClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Report Item")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Bar
                SearchTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = "Search items, locations...",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Tabs (LOST, FOUND, RESOLVED)
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    LostFoundTab.entries.forEach { tab ->
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

                // Filter & Sort Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Chip
                    item {
                        Box {
                            AssistChip(
                                onClick = { showSortMenu = true },
                                label = { Text(uiState.selectedSort) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null, Modifier.size(18.dp)) },
                                shape = MaterialTheme.shapes.medium
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

                if (uiState.error != null) {
                    EmptyState(
                        message = uiState.error ?: "Error occurred",
                        buttonText = "Retry",
                        onButtonClick = viewModel::refresh,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (uiState.isLoading && !uiState.isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.isEmpty) {
                    EmptyState(
                        message = "No items reported yet.",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.filteredItems,
                            key = { it.id }
                        ) { item ->
                            LostFoundCard(
                                item = item,
                                fullWidth = true,
                                onClick = { onItemClick(item.id) },
                                onContactClick = {
                                    val phone = item.contactPhone
                                    if (!phone.isNullOrBlank()) {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
