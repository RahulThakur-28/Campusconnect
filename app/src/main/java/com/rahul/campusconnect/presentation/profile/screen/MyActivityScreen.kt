package com.rahul.campusconnect.presentation.profile.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.discussion.components.QuestionCard
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
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
    onAnnouncementClick: (String) -> Unit,
    onDiscussionClick: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Notes", "Events", "Placements", "Lost & Found", "Announcements", "Discussions")
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
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    edgePadding = 16.dp
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                EmptyState(
                    message = uiState.error ?: "An unexpected error occurred",
                    buttonText = "Retry",
                    onButtonClick = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                )
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
                                items(uiState.myNotes, key = { it.id }) { note ->
                                    NoteCard(note = note, onClick = { onNoteClick(note.id) })
                                }
                            }
                        }
                        1 -> { // Events
                            if (uiState.myEvents.isEmpty()) {
                                item { EmptyState(message = "No events joined yet.") }
                            } else {
                                items(uiState.myEvents, key = { it.id }) { event ->
                                    EventCard(event = event, isRegistered = true, onClick = { onEventClick(event.id) })
                                }
                            }
                        }
                        2 -> { // Placements
                            if (uiState.myPlacements.isEmpty()) {
                                item { EmptyState(message = "No placement applications.") }
                            } else {
                                items(uiState.myPlacements, key = { it.id }) { placement ->
                                    PlacementCard(placement = placement, onClick = { onPlacementClick(placement.id) })
                                }
                            }
                        }
                        3 -> { // Lost & Found
                            if (uiState.myLostFoundItems.isEmpty()) {
                                item { EmptyState(message = "No lost & found posts.") }
                            } else {
                                items(uiState.myLostFoundItems, key = { it.id }) { item ->
                                    LostFoundCard(item = item, fullWidth = true, onClick = { onLostFoundClick(item.id) })
                                }
                            }
                        }
                        4 -> { // Announcements
                            if (uiState.myAnnouncements.isEmpty()) {
                                item { EmptyState(message = "No announcements published.") }
                            } else {
                                items(uiState.myAnnouncements, key = { it.id }) { announcement ->
                                    AnnouncementCard(
                                        announcement = announcement,
                                        onCardClick = { onAnnouncementClick(announcement.id) }
                                    )
                                }
                            }
                        }
                        5 -> { // Discussions
                            if (uiState.myQuestions.isEmpty()) {
                                item { EmptyState(message = "No questions asked.") }
                            } else {
                                items(uiState.myQuestions, key = { it.id }) { question ->
                                    QuestionCard(
                                        question = question,
                                        onLikeClick = { /* Handled in details screen */ },
                                        onViewDiscussionClick = { onDiscussionClick(question.id) }
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}
