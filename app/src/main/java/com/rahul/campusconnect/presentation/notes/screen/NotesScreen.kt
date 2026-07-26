package com.rahul.campusconnect.presentation.notes.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
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
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.presentation.notes.viewmodel.NotesViewModel
import com.rahul.campusconnect.ui.components.*

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Study Notes", fontWeight = FontWeight.Bold) },
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
                FloatingActionButton(
                    onClick = onUploadClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload Note")
                }
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
                    placeholder = "Search subjects, titles or tags...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Unified Filter Row
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
                                leadingIcon = { Icon(Icons.Default.Sort, null, Modifier.size(18.dp)) },
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
                    
                    // File Types
                    items(uiState.fileTypes) { type ->
                        CategoryChip(
                            category = type,
                            isSelected = uiState.selectedFileType == type,
                            onClick = { viewModel.setFilters(fileType = type) }
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
                        message = "No notes found matching your criteria.",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.filteredNotes,
                            key = { it.id }
                        ) { note ->
                            NoteCard(
                                note = note,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onNoteClick(note.id) },
                                onDownload = { onNoteClick(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
