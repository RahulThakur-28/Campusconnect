package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.event.viewmodel.EventListType
import com.rahul.campusconnect.presentation.event.viewmodel.EventsViewModel
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.EventCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    type: EventListType,
    onBackClick: () -> Unit,
    onEventClick: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val events = when (type) {
        EventListType.UPCOMING -> uiState.events.filter { it.startDate > System.currentTimeMillis() }
        EventListType.PAST -> uiState.events.filter { it.endDate < System.currentTimeMillis() }
    }

    val title = when (type) {
        EventListType.UPCOMING -> "Upcoming Events"
        EventListType.PAST -> "Past Events"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (events.isEmpty()) {
            EmptyState(message = "No events found", modifier = Modifier.padding(innerPadding).fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        cardStyle = EventCardStyle.Large,
                        onClick = { onEventClick(event.id) }
                    )
                }
            }
        }
    }
}
