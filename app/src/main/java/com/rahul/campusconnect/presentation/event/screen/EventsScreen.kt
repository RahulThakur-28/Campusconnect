package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.model.Event
import com.rahul.campusconnect.model.UserRole
import com.rahul.campusconnect.presentation.event.components.*
import com.rahul.campusconnect.presentation.event.viewmodel.EventsViewModel
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.EventCardStyle
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel(),
            onUpcomingEventsClick: () -> Unit,
onPastEventsClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredEvents = uiState.events.filter { event ->

        val query = uiState.searchQuery.trim()

        query.isBlank() ||
                event.title.contains(query, ignoreCase = true) ||
                event.description.contains(query, ignoreCase = true) ||
                event.category.contains(query, ignoreCase = true) ||
                event.venue.contains(query, ignoreCase = true)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Events",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: Navigate to Notifications
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },



        floatingActionButton = {

            AnimatedVisibility(
                visible = uiState.canCreateEvent
            ) {
                FloatingActionButton(
                    onClick = onCreateEventClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Event"
                    )
                }
            }
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // Search Bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    placeholder = {
                        Text("Search events...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
            }

            // Categories
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = uiState.selectedCategory == category,
                            onClick = {
                                viewModel.onCategorySelected(category)
                            }
                        )
                    }
                }
            }

            when {

                uiState.isLoading -> {

                    item {
                        LoadingShimmer()
                    }

                }

                uiState.events.isEmpty() -> {

                    item {
                        EmptyEventsState()
                    }

                }

                else -> {

                    // Featured Event
                    uiState.featuredEvent?.let { featured ->

                        item {

                            SectionHeader(
                                title = "Featured",
                                actionText = null
                            )

                            FeaturedEventCard(
                                event = featured,
                                onClick = {
                                    onEventClick(featured.id)
                                },
                                onRegisterClick = {
                                    viewModel.onRegisterEvent(featured.id)
                                }
                            )

                        }
                    }

                    // Registered Events
                    val registeredEvents =
                        filteredEvents.filter {
                            uiState.registeredEventIds.contains(it.id)
                        }

                    if (registeredEvents.isNotEmpty()) {

                        item {

                            Spacer(Modifier.height(24.dp))

                            SectionHeader(
                                title = "My Registered Events"
                            )

                        }

                        item {

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                items(registeredEvents) { event ->

                                    EventCard(
                                        event = event,
                                        isRegistered = true,
                                        cardStyle = EventCardStyle.Medium,
                                        onClick = {
                                            onEventClick(event.id)
                                        }
                                    )

                                }

                            }

                        }

                    }

                    // Upcoming Events
                val upcomingEvents = filteredEvents.filter { !it.isFeatured }

                    if (upcomingEvents.isNotEmpty()) {

                        item {

                            Spacer(Modifier.height(24.dp))

                            SectionHeader(
                                title = "Upcoming Events",
                                actionText = "See All",
                                onActionClick = onUpcomingEventsClick
                            )

                        }

                        items(upcomingEvents) { event ->

                            EventCard(
                                event = event,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                cardStyle = EventCardStyle.Large,
                                showRegisterButton = true,
                                isRegistered = uiState.registeredEventIds.contains(event.id),
                                onRegisterClick = {
                                    viewModel.onRegisterEvent(event.id)
                                },
                                onClick = {
                                    onEventClick(event.id)
                                }
                            )

                        }

                    }

                    // Past Events
                    item {

                        Spacer(Modifier.height(24.dp))

                        SectionHeader(
                            title = "Past Events",
                            actionText = "See All",
                            onActionClick = onPastEventsClick
                        )

                    }

                    item {

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            items(3) {

                                val pastEvent = Event(
                                    id = "past_1",
                                    title = "Hackathon 2023",
                                    category = "Technical",
                                    date = "Dec 10, 2023",
                                    isRegistrationOpen = false
                                )

                                EventCard(
                                    event = pastEvent,
                                    onClick = {
                                        onEventClick(pastEvent.id)
                                    },
                                    cardStyle = EventCardStyle.Small,
                                    showRegisterButton = false,
                                    showAttendance = false
                                )

                            }

                        }

                    }

                }

            }

        }

    }
}

