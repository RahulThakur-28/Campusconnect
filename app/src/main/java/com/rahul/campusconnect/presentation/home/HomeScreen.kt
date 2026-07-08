package com.rahul.campusconnect.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.rahul.campusconnect.model.Announcement
import com.rahul.campusconnect.ui.components.*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(
    state: HomeUiState = dummyHomeState()
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
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Header
            item {
                HomeHeader(
                    userName = state.userName,
                    department = state.department,
                    academicYear = state.academicYear,
                    isVerified = state.isVerified,
                    notificationCount = state.notificationCount,
                    onNotificationClick = { /* Handle Notifications */ }
                )
            }

            // 2. Search Bar
            item {
                SearchBar(modifier = Modifier.padding(bottom = 16.dp))
            }

            // 3. Latest Announcement
            item {
                SectionHeader(title = "Latest Announcement")
                if (state.announcements.isNotEmpty()) {
                    AnnouncementCard(announcement = state.announcements.first())
                } else {
                    EmptyState(message = "No announcements yet")
                }
            }

            // 4. Upcoming Events
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Upcoming Events")
                if (state.events.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.events) { event ->
                            EventCard(event = event)
                        }
                    }
                } else {
                    EmptyState(message = "No upcoming events")
                }
            }

            // 5. Upcoming Placements
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Upcoming Placements")
                if (state.placements.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.placements) { placement ->
                            PlacementCard(placement = placement)
                        }
                    }
                } else {
                    EmptyState(message = "No placement drives active")
                }
            }
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector)

private fun dummyHomeState() = HomeUiState(
    userName = "Rahul",
    department = "CSE",
    academicYear = "3rd Year",
    isVerified = true,
    notificationCount = 3,
    announcements = listOf(
        Announcement(
            "1",
            "End Semester Examination Schedule",
            "The end semester examination for 3rd year students will commence from June 15th. Detailed schedule attached.",
            "Placements"
        )
    ),
    events = listOf(
        Event("1", "TechQuest 2024", "Oct 24, 2024", "Auditorium A", status = "Registration Open"),
        Event("2", "AI Workshop", "Nov 02, 2024", "Lab 104", status = "Limited Seats"),
        Event("3", "Cultural Fest", "Dec 12, 2024", "Main Ground", status = "Coming Soon")
    ),
    placements = listOf(
        Placement("1", "Google", null, "Software Engineer", "45 LPA", "Oct 30", status = "Active"),
        Placement("2", "Microsoft", null, "SDE Intern", "1.5L/pm", "Nov 05", status = "Applied"),
        Placement("3", "Atlassian", null, "Associate Engineer", "55 LPA", "Nov 12", status = "Active")
    )
)
