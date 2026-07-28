package com.rahul.campusconnect.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.navigateToAnnouncementDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.home.viewmodel.HomeViewModel
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notification.navigation.navigateToNotifications
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.search.navigation.navigateToSearch
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = uiState,
        onRefresh = viewModel::refresh,
        onNotificationClick = { navController.navigateToNotifications() },
        onSearchClick = { navController.navigateToSearch() },
        onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
        onActionClick = { route -> navController.navigate(route) },
        onAnnouncementClick = { id -> navController.navigateToAnnouncementDetails(id) },
        onEventClick = { id -> navController.navigateToEventDetails(id) },
        onPlacementClick = { id -> navController.navigateToPlacementDetails(id) },
        onNoteClick = { id -> navController.navigateToNoteDetails(id) },
        onLostFoundClick = { id -> navController.navigateToLostFoundDetails(id) },
        onSeeAllAnnouncements = { navController.navigate(AppRoutes.Announcements.route) },
        onSeeAllEvents = { navController.navigate(AppRoutes.Events.route) },
        onSeeAllPlacements = { navController.navigate(AppRoutes.Placements.route) },
        onSeeAllNotes = { navController.navigate(AppRoutes.Notes.route) },
        onSeeAllLostFound = { navController.navigate(AppRoutes.LostFound.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onRefresh: () -> Unit,
    onNotificationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onActionClick: (String) -> Unit,
    onAnnouncementClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onPlacementClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
    onLostFoundClick: (String) -> Unit,
    onSeeAllAnnouncements: () -> Unit,
    onSeeAllEvents: () -> Unit,
    onSeeAllPlacements: () -> Unit,
    onSeeAllNotes: () -> Unit,
    onSeeAllLostFound: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ---------------- Header ----------------
            item {
                HomeHeader(
                    userName = state.userName,
                    department = state.department,
                    academicYear = state.academicYear,
                    isVerified = state.isVerified,
                    profileImageUrl = state.profileImageUrl,
                    greeting = state.greeting,
                    notificationCount = state.notificationCount,
                    onNotificationClick = onNotificationClick,
                    onProfileClick = onProfileClick
                )
            }

            // ---------------- Search ----------------
            item {
                SearchBar(
                    hint = "Search CampusConnect...",
                    readOnly = true,
                    onClick = onSearchClick,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                )
            }

            // ---------------- Quick Actions ----------------


            if (state.isLoading && !state.isRefreshing) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(0.6f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.error != null) {
                item {
                    EmptyState(
                        message = "Something went wrong",
                        description = state.error,
                        buttonText = "Retry",
                        onButtonClick = onRefresh,
                        modifier = Modifier.fillParentMaxHeight(0.5f)
                    )
                }
            } else {
                // ---------------- Latest Announcements ----------------
                item {
                    HomeSection(
                        title = "Latest Announcements",
                        onSeeAllClick = onSeeAllAnnouncements
                    ) {
                        if (state.announcements.isNotEmpty()) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                state.announcements.forEach { announcement ->
                                    AnnouncementCard(
                                        announcement = announcement,
                                        onCardClick = { onAnnouncementClick(announcement.id) },
                                        onReadMoreClick = { onAnnouncementClick(announcement.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No announcements yet", modifier = Modifier.height(150.dp))
                        }
                    }
                }

                // ---------------- Upcoming Events ----------------
                item {
                    HomeSection(
                        title = "Upcoming Events",
                        onSeeAllClick = onSeeAllEvents
                    ) {
                        if (state.events.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.events, key = { it.id }) { event ->
                                    EventCard(
                                        event = event,
                                        onClick = { onEventClick(event.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No upcoming events", modifier = Modifier.height(150.dp))
                        }
                    }
                }

                // ---------------- Placement Updates ----------------
                item {
                    HomeSection(
                        title = "Placement Updates",
                        onSeeAllClick = onSeeAllPlacements
                    ) {
                        if (state.placements.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.placements, key = { it.id }) { placement ->
                                    PlacementCard(
                                        placement = placement,
                                        onClick = { onPlacementClick(placement.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No active drives", modifier = Modifier.height(150.dp))
                        }
                    }
                }

                // ---------------- Trending Notes ----------------
                item {
                    HomeSection(
                        title = "Trending Notes",
                        onSeeAllClick = onSeeAllNotes
                    ) {
                        if (state.notes.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.notes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) },
                                        onViewNotes = { onNoteClick(note.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No trending notes", modifier = Modifier.height(150.dp))
                        }
                    }
                }

                // ---------------- Lost & Found ----------------
                item {
                    HomeSection(
                        title = "Lost & Found",
                        onSeeAllClick = onSeeAllLostFound
                    ) {
                        if (state.lostFoundItems.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.lostFoundItems, key = { it.id }) { item ->
                                    LostFoundCard(
                                        item = item,
                                        onClick = { onLostFoundClick(item.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No items reported", modifier = Modifier.height(150.dp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(48.dp)) }
        }
    }
}
