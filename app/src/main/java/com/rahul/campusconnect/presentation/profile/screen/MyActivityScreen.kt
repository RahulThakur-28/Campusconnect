package com.rahul.campusconnect.presentation.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val tabs = listOf("Notes", "Events", "Placements", "Found", "Notice", "Q&A")
    val tabKeys = listOf("Notes", "Events", "Placements", "Lost & Found", "Announcements", "Discussions")
    
    var selectedTabIndex by remember { 
        mutableStateOf(tabKeys.indexOf(initialTab).coerceAtLeast(0)) 
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = { 
                        Text(
                            "My Contributions", 
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    edgePadding = 24.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    title, 
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            }
                        )
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            } else if (uiState.error != null) {
                EmptyState(
                    message = "Connection Error",
                    description = uiState.error,
                    buttonText = "Retry",
                    onButtonClick = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> { // Notes
                            if (uiState.myNotes.isEmpty()) {
                                item { 
                                    EmptyState(
                                        message = "No notes uploaded", 
                                        description = "Start sharing your study materials with the community.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
                            } else {
                                items(uiState.myNotes, key = { it.id }) { note ->
                                    NoteCard(note = note, onClick = { onNoteClick(note.id) })
                                }
                            }
                        }
                        1 -> { // Events
                            if (uiState.myEvents.isEmpty()) {
                                item { 
                                    EmptyState(
                                        message = "No events joined", 
                                        description = "Your registered campus events will appear here.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
                            } else {
                                items(uiState.myEvents, key = { it.id }) { event ->
                                    EventCard(event = event, isRegistered = true, onClick = { onEventClick(event.id) })
                                }
                            }
                        }
                        2 -> { // Placements
                            if (uiState.myPlacements.isEmpty()) {
                                item { 
                                    EmptyState(
                                        message = "No applications", 
                                        description = "Track your career opportunities and applications here.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
                            } else {
                                items(uiState.myPlacements, key = { it.id }) { placement ->
                                    PlacementCard(placement = placement, onClick = { onPlacementClick(placement.id) })
                                }
                            }
                        }
                        3 -> { // Lost & Found
                            if (uiState.myLostFoundItems.isEmpty()) {
                                item { 
                                    EmptyState(
                                        message = "No items reported", 
                                        description = "Found something? Report it to help someone find their lost items.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
                            } else {
                                items(uiState.myLostFoundItems, key = { it.id }) { item ->
                                    LostFoundCard(
                                        item = item,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { onLostFoundClick(item.id) }
                                    )
                                }
                            }
                        }
                        4 -> { // Announcements
                            if (uiState.myAnnouncements.isEmpty()) {
                                item { 
                                    EmptyState(
                                        message = "No notices published", 
                                        description = "Announcements you share will be listed here.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
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
                                item { 
                                    EmptyState(
                                        message = "No active discussions", 
                                        description = "Ask a question or participate in campus conversations.",
                                        modifier = Modifier.fillParentMaxHeight(0.8f)
                                    ) 
                                }
                            } else {
                                items(uiState.myQuestions, key = { it.discussionId }) { discussion ->
                                    QuestionCard(
                                        discussion = discussion,
                                        currentUserId = uiState.user.uid,
                                        onLikeClick = { /* Handled in details screen */ },
                                        onReplyClick = { onDiscussionClick(discussion.discussionId) },
                                        onEditClick = { onDiscussionClick(discussion.discussionId) },
                                        onDeleteClick = { viewModel.deleteDiscussion(discussion.discussionId) },
                                        onReportClick = { /* No-op for personal content */ },
                                        onCardClick = { onDiscussionClick(discussion.discussionId) }
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
