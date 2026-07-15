package com.rahul.campusconnect.presentation.notes.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.model.UserRole
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.presentation.notes.viewmodel.NotesViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val semesters = listOf("All", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th")
    val departments = listOf("All", "CSE", "IT", "ECE", "ME", "CE")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Notes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (uiState.userRole in listOf(
                    UserRole.VERIFIED_STUDENT,
                    UserRole.VERIFIED_TEACHER,
                    UserRole.ADMIN
                )
            ) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Search
            item {
                SearchBar(
                    hint = "Search by subject or title..."
                )
            }

            // Semester
            item {
                SectionHeader(title = "Semester", actionText = null)
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(semesters) { semester ->
                        CategoryChip(
                            category = semester,
                            isSelected = uiState.selectedSemester == semester,
                            onClick = {
                                viewModel.onSemesterSelected(semester)
                            }
                        )
                    }
                }
            }

            // Department
            item {
                SectionHeader(title = "Department", actionText = null)
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(departments) { dept ->
                        CategoryChip(
                            category = dept,
                            isSelected = uiState.selectedDepartment == dept,
                            onClick = {
                                viewModel.onDepartmentSelected(dept)
                            }
                        )
                    }
                }
            }

            // Loading
            if (uiState.isLoading) {

                item {
                    Box(
                        modifier = Modifier.fillParentMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }
            // Empty State
            else if (uiState.filteredNotes.isEmpty()) {

                item {
                    EmptyState(
                        message =
                                "No Notes Found\n" +
                                "\n" +
                                "Try changing filters\n" +
                                "or search keywords."
                    )
                }

            }
            // Notes
            else {

                items(uiState.filteredNotes) { note ->

                    NoteCard(
                        note = note,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNoteClick(note.id)
                        },
                        onDownload = {
                            // TODO
                        }
                    )

                }
            }
        }
    }
}
