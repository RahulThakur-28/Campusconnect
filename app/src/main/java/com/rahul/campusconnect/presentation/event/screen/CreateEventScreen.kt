package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rahul.campusconnect.presentation.event.components.EventForm

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit,
    onEventCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Event")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        EventForm(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            title = title,
            onTitleChange = { title = it },

            description = description,
            onDescriptionChange = { description = it },

            category = category,
            onCategoryChange = { category = it },

            date = date,
            onDateChange = { date = it },

            venue = venue,
            onVenueChange = { venue = it },

            buttonText = "Publish Event",

            onSubmit = onEventCreated
        )
    }
}