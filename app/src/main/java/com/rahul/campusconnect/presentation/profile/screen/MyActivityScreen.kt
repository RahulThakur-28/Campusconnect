package com.rahul.campusconnect.presentation.profile.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.presentation.profile.ProfileViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyActivityScreen(
    initialTab: String = "Notes",
    onBackClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onPlacementClick: (String) -> Unit,
    onLostFoundClick: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Notes", "Events", "Placements", "Lost & Found")
    var selectedTabIndex by remember { 
        mutableStateOf(tabs.indexOf(initialTab).coerceAtLeast(0)) 
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("My Activity", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> { // Notes
                            if (uiState.myNotes.isEmpty()) {
                                item { EmptyState(message = "No notes uploaded yet.") }
                            } else {
                                items(uiState.myNotes) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) }
                                    )
                                }
                            }
                        }
                        1 -> { // Events
                            if (uiState.myEvents.isEmpty()) {
                                item { EmptyState(message = "No events joined yet.") }
                            } else {
                                items(uiState.myEvents) { event ->
                                    EventCard(
                                        event = event,
                                        isRegistered = true,
                                        onClick = { onEventClick(event.id) }
                                    )
                                }
                            }
                        }
                        2 -> { // Placements
                            if (uiState.myPlacements.isEmpty()) {
                                item { EmptyState(message = "No placement applications.") }
                            } else {
                                items(uiState.myPlacements) { placement ->
                                    PlacementCard(
                                        placement = placement,
                                        onClick = { onPlacementClick(placement.id) }
                                    )
                                }
                            }
                        }
                        3 -> { // Lost & Found
                            if (uiState.myLostFoundItems.isEmpty()) {
                                item { EmptyState(message = "No lost & found posts.") }
                            } else {
                                items(uiState.myLostFoundItems) { item ->
                                    LostFoundCard(
                                        item = item,
                                        fullWidth = true,
                                        onClick = { onLostFoundClick(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
