package com.rahul.campusconnect.presentation.event.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.model.Event
import com.rahul.campusconnect.presentation.event.viewmodel.EventListType
import com.rahul.campusconnect.presentation.event.viewmodel.EventsViewModel
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
        val uiState by viewModel.uiState.collectAsState()

        val events = when (type) {
            EventListType.UPCOMING ->
                uiState.events.filter { !it.isFeatured }

            EventListType.PAST ->
                uiState.events.filter { !it.isRegistrationOpen }
        }

    val title = when (type) {
        EventListType.UPCOMING -> "Upcoming Events"
        EventListType.PAST -> "Past Events"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(
                items = events,
                key = { event -> event.id }
            ) { event ->

                EventCard(
                    event = event,
                    cardStyle = EventCardStyle.Large,

                    isRegistered =

                            uiState.registeredEventIds.contains(event.id),

                    showRegisterButton =
                        type == EventListType.UPCOMING,

                    showAttendance = true,

                    onClick = {
                        onEventClick(event.id)
                    },

                    onRegisterClick = {
                        if (type == EventListType.UPCOMING) {
                            viewModel.onRegisterEvent(event.id)
                        }
                    }
                )
            }
        }
    }
}