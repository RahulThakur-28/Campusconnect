package com.rahul.campusconnect.presentation.event.screen

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.event.components.EventForm
import com.rahul.campusconnect.presentation.event.viewmodel.EditEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onEventUpdated: () -> Unit,
    viewModel: EditEventViewModel = hiltViewModel(),

){

    // Dummy data for now.
    // Later load event using eventId from ViewModel / Repository

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }



    var bannerUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var title by remember {
        mutableStateOf("TechQuest 2024")
    }

    var description by remember {
        mutableStateOf(
            "Annual coding competition for all engineering students."
        )
    }

    var category by remember {
        mutableStateOf("Technical")
    }

    var date by remember {
        mutableStateOf("24 Oct 2026")
    }

    var time by remember {
        mutableStateOf("10:00 AM")
    }

    var venue by remember {
        mutableStateOf("Auditorium A")
    }

    var showErrors by remember {
        mutableStateOf(false)
    }


    // ------------------------------------------------
    // Validation
    // ------------------------------------------------

    LaunchedEffect(uiState.event) {

        uiState.event?.let { event ->

            title = event.title
            description = event.description
            category = event.category
            date = event.date
            time = event.time
            venue = event.venue

        }
    }

    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            onEventUpdated()

            viewModel.clearSuccess()
        }
    }


    val titleError = when {
        !showErrors -> null
        title.isBlank() ->
            "Event title is required"

        title.length < 3 ->
            "Title must be at least 3 characters"

        title.length > 100 ->
            "Title cannot exceed 100 characters"

        else -> null
    }


    val descriptionError = when {
        !showErrors -> null
        description.isBlank() ->
            "Description is required"

        description.length < 10 ->
            "Description must be at least 10 characters"

        description.length > 1000 ->
            "Description cannot exceed 1000 characters"

        else -> null
    }


    val categoryError = when {
        !showErrors -> null

        category.isBlank() ->
            "Please select an event category"

        else -> null
    }


    val dateError = when {
        !showErrors -> null

        date.isBlank() ->
            "Please select an event date"

        else -> null
    }


    val timeError = when {
        !showErrors -> null

        time.isBlank() ->
            "Please select an event time"

        else -> null
    }


    val venueError = when {
        !showErrors -> null

        venue.isBlank() ->
            "Event venue is required"

        venue.length < 3 ->
            "Please enter a valid venue"

        else -> null
    }


    val isFormValid =
        title.length in 3..100 &&
                description.length in 10..1000 &&
                category.isNotBlank() &&
                date.isNotBlank() &&
                time.isNotBlank() &&
                venue.length >= 3


    // ------------------------------------------------
    // UI
    // ------------------------------------------------

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Edit Event")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,

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

            // Title
            title = title,
            onTitleChange = {
                title = it
            },
            titleError = titleError,


            // Description
            description = description,
            onDescriptionChange = {
                description = it
            },
            descriptionError = descriptionError,


            // Category
            category = category,
            onCategoryChange = {
                category = it
            },
            categoryError = categoryError,


            // Date
            date = date,
            onDateChange = {
                date = it
            },
            dateError = dateError,


            // Time
            time = time,
            onTimeChange = {
                time = it
            },
            timeError = timeError,


            // Venue
            venue = venue,
            onVenueChange = {
                venue = it
            },
            venueError = venueError,


            // Button
            // Button
            buttonText = "Update Event",

            imageUri = bannerUri,
            imageUrl = uiState.event?.imageUrl,

            onImageSelected = { uri ->
                bannerUri = uri
            },

            onRemoveImage = {
                bannerUri = null
            },

            onSubmit = {

                showErrors = true

                if (isFormValid) {

                    uiState.event?.let { currentEvent ->

                        viewModel.updateEvent(

                            event = currentEvent.copy(

                                title = title,
                                description = description,
                                category = category,
                                date = date,
                                time = time,
                                venue = venue

                            ),

                            imageUri = bannerUri

                        )
                    }
                }
            }
        )
    }
}