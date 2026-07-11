package com.rahul.campusconnect.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.ui.components.AnnouncementCard
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.HomeHeader
import com.rahul.campusconnect.ui.components.HomeSection
import com.rahul.campusconnect.ui.components.LostFoundCard
import com.rahul.campusconnect.ui.components.PlacementCard
import com.rahul.campusconnect.ui.components.SearchBar
import com.rahul.campusconnect.ui.components.TrendingNotesCard


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(
    state: HomeUiState =dummyHomeState()
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        NavigationItem("Home", Icons.Default.Home),
        NavigationItem("Placements", Icons.Default.Work),
        NavigationItem("Events", Icons.Default.Event),
        NavigationItem("Announcements", Icons.Default.Campaign),
        NavigationItem("More", Icons.Default.MoreHoriz)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {

                tabs.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )

                }

            }

        }

    ) { innerPadding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            contentPadding = PaddingValues(bottom = 32.dp)

        ) {

            // Header

            item {

                HomeHeader(
                    userName = state.userName,
                    department = state.department,
                    academicYear = state.academicYear,
                    isVerified = state.isVerified,
                    notificationCount = state.notificationCount,
                    onNotificationClick = {}
                )

            }

            // Search

            item {

                Spacer(Modifier.height(8.dp))

                SearchBar()

            }

            // Announcement

            item {

                HomeSection(
                    title = "Latest Announcement"
                ) {

                    if (state.announcements.isNotEmpty()) {

                        AnnouncementCard(
                            announcement = state.announcements.first()
                        )

                    } else {

                        EmptyState(
                            message = "No announcements yet"
                        )

                    }

                }

            }

            // Events

            item {

                HomeSection(
                    title = "Upcoming Events"
                ) {

                    if (state.events.isNotEmpty()) {

                        LazyRow(

                            contentPadding = PaddingValues(horizontal = 20.dp),

                            horizontalArrangement = Arrangement.spacedBy(16.dp)

                        ) {

                            items(
                                items = state.events,
                                key = { it.id }
                            ) { event ->

                                EventCard(
                                    event = event
                                )

                            }

                        }

                    } else {

                        EmptyState(
                            "No upcoming events"
                        )

                    }

                }

            }

            // Placements

            item {

                HomeSection(
                    title = "Placement Updates"
                ) {

                    if (state.placements.isNotEmpty()) {

                        LazyRow(

                            contentPadding = PaddingValues(horizontal = 20.dp),

                            horizontalArrangement = Arrangement.spacedBy(16.dp)

                        ) {

                            items(
                                state.placements,
                                key = { it.id }
                            ) { placement ->

                                PlacementCard(
                                    placement = placement
                                )

                            }

                        }

                    } else {

                        EmptyState(
                            "No placement drives active"
                        )

                    }

                }

            }
            // Trending Notes

            item {

                HomeSection(
                    title = "Trending Notes"
                ) {

                    if (state.notes.isNotEmpty()) {

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            items(
                                items = state.notes,
                                key = { it.id }
                            ) { note ->

                                TrendingNotesCard(
                                    note = note
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

            // Lost & Found

            item {

                HomeSection(
                    title = "Lost & Found"
                ) {

                    if (state.lostFoundItems.isNotEmpty()) {

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            items(
                                items = state.lostFoundItems,
                                key = { it.id }
                            ) { item ->

                                LostFoundCard(
                                    item = item
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

            // Bottom Space

            item {

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

            }

        }

    }

}

data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

