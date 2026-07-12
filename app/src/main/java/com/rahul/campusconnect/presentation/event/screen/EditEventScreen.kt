package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rahul.campusconnect.presentation.event.components.EventForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onEventUpdated: () -> Unit
) {

    // Dummy data (later this will come from ViewModel)
    var title by remember { mutableStateOf("TechQuest 2024") }
    var description by remember {
        mutableStateOf("Annual coding competition for all engineering students.")
    }
    var category by remember { mutableStateOf("Technical") }
    var date by remember { mutableStateOf("24 Oct 2026") }
    var venue by remember { mutableStateOf("Auditorium A") }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Event")
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

            buttonText = "Update Event",

            onSubmit = onEventUpdated

        )
    }
}