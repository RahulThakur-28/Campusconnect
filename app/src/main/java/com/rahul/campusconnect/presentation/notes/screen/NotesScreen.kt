package com.rahul.campusconnect.presentation.notes.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
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
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.presentation.notes.viewmodel.NotesViewModel
import com.rahul.campusconnect.ui.components.*
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onBackClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
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
                    msg.contains("success") || msg.contains("successfully") || msg.contains("started") -> SuccessGreen
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
                        text = "Study Notes", 
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
            if (uiState.userRole != UserRole.STUDENT) {
                ExtendedFloatingActionButton(
                    onClick = onUploadClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Upload Note", fontWeight = FontWeight.Bold) }
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
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                item {
                    SearchBar(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        hint = "Search subjects, titles or tags...",
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // Filter Row
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentPadding = PaddingValues(),
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
                                    leadingIcon = { Icon(Icons.Default.Sort, null, Modifier.size(18.dp)) },
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
                                                viewModel.setFilters(sort = option)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Semesters
                        items(uiState.semesters) { sem ->
                            CategoryChip(
                                category = if (sem == "All") "All Sem" else "$sem Sem",
                                isSelected = uiState.selectedSemester == sem,
                                onClick = { viewModel.setFilters(semester = sem) }
                            )
                        }

                        // Branches
                        items(uiState.branches) { branch ->
                            CategoryChip(
                                category = branch,
                                isSelected = uiState.selectedBranch == branch,
                                onClick = { viewModel.setFilters(branch = branch) }
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
                            NoteLoadingShimmer()
                        }
                    }

                    uiState.isEmpty -> {
                        item {
                            val emptyMessage = if (uiState.searchQuery.isNotBlank()) {
                                "No results for \"${uiState.searchQuery}\""
                            } else {
                                "No Notes Available"
                            }
                            
                            EmptyState(
                                message = emptyMessage,
                                description = "Try adjusting your search or filters.",
                                modifier = Modifier.fillParentMaxHeight(0.7f)
                            )
                        }
                    }

                    else -> {
                        items(
                            items = uiState.filteredNotes,
                            key = { it.id }
                        ) { note ->
                            NoteCard(
                                note = note,
                                fillMaxWidth = true,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    onNoteClick(note.id)
                                },
                                onViewNotes = {
                                    onNoteClick(note.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
