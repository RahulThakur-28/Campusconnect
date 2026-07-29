package com.rahul.campusconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ---------------- Header ----------------
            item {
                HomeHeader(
                    userName = state.userName,
                    department = state.department,
                    academicYear = state.academicYear,
                    isVerified = state.isVerified,
                    profileImageUrl = state.profileImageUrl,
                    notificationCount = state.notificationCount,
                    onNotificationClick = onNotificationClick,
                    onProfileClick = onProfileClick
                )
            }

            // ---------------- Search ----------------
            item {
                SearchBar(
                    hint = "Search announcements, events...",
                    readOnly = true,
                    onClick = onSearchClick,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            if (state.isLoading && !state.isRefreshing) {
                item {
                    HomeShimmerEffect()
                }
            } else if (state.error != null) {
                item {
                    EmptyState(
                        message = "Oops! Something went wrong",
                        description = state.error,
                        buttonText = "Retry",
                        onButtonClick = onRefresh,
                        modifier = Modifier.fillParentMaxHeight(0.6f)
                    )
                }
            } else {
                // ---------------- Latest Announcements ----------------
                item {
                    val listState = rememberLazyListState()
                    HomeSection(
                        title = "Latest Announcements",
                        itemCount = state.announcementsCount,
                        onSeeAllClick = onSeeAllAnnouncements
                    ) {
                        if (state.announcements.isNotEmpty()) {
                            LazyRow(
                                state = listState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    items = state.announcements.take(5),
                                    key = { it.id }
                                ) { announcement ->
                                    AnnouncementCard(
                                        announcement = announcement,
                                        onCardClick = { onAnnouncementClick(announcement.id) },
                                        modifier = Modifier.width(300.dp) // PEEK effect
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No announcements yet", modifier = Modifier.height(180.dp))
                        }
                    }
                }

                // ---------------- Upcoming Events ----------------
                item {
                    val listState = rememberLazyListState()
                    HomeSection(
                        title = "Upcoming Events",
                        itemCount = state.eventsCount,
                        onSeeAllClick = onSeeAllEvents
                    ) {
                        if (state.events.isNotEmpty()) {
                            LazyRow(
                                state = listState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.events.take(5), key = { it.id }) { event ->
                                    EventCard(
                                        event = event,
                                        onClick = { onEventClick(event.id) },
                                        modifier = Modifier.width(280.dp) // PEEK effect
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No upcoming events", modifier = Modifier.height(180.dp))
                        }
                    }
                }

                // ---------------- Placement Updates ----------------
                item {
                    val listState = rememberLazyListState()
                    HomeSection(
                        title = "Placement Updates",
                        itemCount = state.placementsCount,
                        onSeeAllClick = onSeeAllPlacements
                    ) {
                        if (state.placements.isNotEmpty()) {
                            LazyRow(
                                state = listState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.placements.take(5), key = { it.id }) { placement ->
                                    PlacementCard(
                                        placement = placement,
                                        onClick = { onPlacementClick(placement.id) },
                                        modifier = Modifier.width(280.dp) // PEEK effect
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No active drives", modifier = Modifier.height(180.dp))
                        }
                    }
                }

                // ---------------- Trending Notes ----------------
                item {
                    val listState = rememberLazyListState()
                    HomeSection(
                        title = "Trending Notes",
                        itemCount = state.notesCount,
                        onSeeAllClick = onSeeAllNotes
                    ) {
                        if (state.notes.isNotEmpty()) {
                            LazyRow(
                                state = listState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.notes.take(5), key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) },
                                        onViewNotes = { onNoteClick(note.id) },
                                        modifier = Modifier.width(CardConstants.HomeCardWidth) // Increased width
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No trending notes", modifier = Modifier.height(180.dp))
                        }
                    }
                }

                // ---------------- Lost & Found ----------------
                item {
                    val listState = rememberLazyListState()
                    HomeSection(
                        title = "Lost & Found",
                        itemCount = state.lostFoundItemsCount,
                        onSeeAllClick = onSeeAllLostFound
                    ) {
                        if (state.lostFoundItems.isNotEmpty()) {
                            LazyRow(
                                state = listState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(items = state.lostFoundItems.take(5), key = { it.id }) { item ->
                                    LostFoundCard(
                                        item = item,
                                        onClick = { onLostFoundClick(item.id) },
                                        modifier = Modifier.width(280.dp) // PEEK effect
                                    )
                                }
                            }
                        } else {
                            EmptyState(message = "No items reported", modifier = Modifier.height(180.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeShimmerEffect() {
    Column(modifier = Modifier.padding(24.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            )
        }
    }
}
