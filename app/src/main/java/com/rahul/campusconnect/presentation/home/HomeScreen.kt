package com.rahul.campusconnect.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.navigateToAnnouncementDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.notification.navigation.navigateToNotifications
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.search.navigation.navigateToSearch
import com.rahul.campusconnect.ui.components.AnnouncementCard
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.HomeHeader
import com.rahul.campusconnect.ui.components.HomeSection
import com.rahul.campusconnect.ui.components.LostFoundCard
import com.rahul.campusconnect.ui.components.NoteCard
import com.rahul.campusconnect.ui.components.PlacementCard
import com.rahul.campusconnect.ui.components.SearchBar

@Composable
fun HomeScreen(
    navController: NavController,
    state: HomeUiState = dummyHomeState()
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
                notificationCount = state.notificationCount,
                onNotificationClick = {
                    navController.navigateToNotifications()
                }
            )
        }


        // ---------------- Search ----------------

        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SearchBar(
                onClick = {
                    navController.navigateToSearch()
                }
            )
        }


        // ---------------- Latest Announcement ----------------

        item {
            HomeSection(
                title = "Latest Announcement",
                onSeeAllClick = {
                    navController.navigate(
                        AppRoutes.Announcements.route
                    )
                }
            )  {

                if (state.announcements.isNotEmpty()) {

                    val announcement = state.announcements.first()

                    AnnouncementCard(
                        announcement = announcement,
                        onCardClick = {
                            navController.navigateToAnnouncementDetails(
                                announcement.id
                            )
                        },
                        onReadMoreClick = {
                            navController.navigateToAnnouncementDetails(
                                announcement.id
                            )
                        }
                    )

                } else {

                    EmptyState(
                        message = "No announcements yet"
                    )
                }
            }
        }


        // ---------------- Upcoming Events ----------------

        item {
            HomeSection(
                title = "Upcoming Events",
                onSeeAllClick = {
                    navController.navigate(
                        AppRoutes.Events.route
                    )
                }
            ) {

                if (state.events.isNotEmpty()) {

                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = 20.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(
                            items = state.events,
                            key = { event -> event.id }
                        ) { event ->

                            EventCard(
                                event = event,
                                onClick = {
                                    navController.navigateToEventDetails(event.id)
                                }
                            )
                        }
                    }

                } else {

                    EmptyState(
                        message = "No upcoming events"
                    )
                }
            }
        }


        // ---------------- Placement Updates ----------------

        item {
            HomeSection(
                title = "Placement Updates",
                onSeeAllClick = {
                    navController.navigate(
                        AppRoutes.Placements.route
                    )
                }
            ) {

                if (state.placements.isNotEmpty()) {

                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = 20.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(
                            items = state.placements,
                            key = { placement -> placement.id }
                        ) { placement ->

                            PlacementCard(
                                placement = placement,
                                onClick = {
                                    navController.navigateToPlacementDetails(placementId = placement.id)
                                }
                            )
                        }
                    }

                } else {

                    EmptyState(
                        message = "No placement drives active"
                    )
                }
            }
        }


        // ---------------- Trending Notes ----------------

        item {
            HomeSection(
                title = "Trending Notes",
                onSeeAllClick ={
                    navController.navigate(AppRoutes.Notes.route)

                }

            ) {

                if (state.notes.isNotEmpty()) {

                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = 20.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(
                            items = state.notes,
                            key = { note -> note.id }
                        ) { note ->

                            NoteCard(
                                note = note,
                                onClick = {
                                    navController.navigate(AppRoutes.NoteDetails.route)
                                }
                            )
                        }
                    }

                } else {

                    EmptyState(
                        message = "No trending notes"
                    )
                }
            }
        }


        // ---------------- Lost & Found ----------------

        item {
            HomeSection(
                title = "Lost & Found",
                onSeeAllClick = {
                    navController.navigate(AppRoutes.LostFound.route)
                }
            ) {

                if (state.lostFoundItems.isNotEmpty()) {

                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = 20.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(
                            items = state.lostFoundItems,
                            key = { item -> item.id }
                        ) { item ->

                            LostFoundCard(
                                item = item,
                                onClick ={
                                    navController.navigate(AppRoutes.LostFoundDetails.route)
                                }
                            )
                        }
                    }

                } else {

                    EmptyState(
                        message = "No items reported"
                    )
                }
            }
        }


        // ---------------- Bottom Spacing ----------------

        item {
            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HomeScreenPreview() {

    HomeScreen(
        navController = rememberNavController()
    )
}