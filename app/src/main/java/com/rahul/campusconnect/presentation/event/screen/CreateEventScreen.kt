package com.rahul.campusconnect.presentation.event.screen

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.event.components.EventForm
import com.rahul.campusconnect.presentation.event.viewmodel.CreateEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit,
    onEventCreated: () -> Unit

) {

    val viewModel: CreateEventViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    var bannerUri by remember {
        mutableStateOf<Uri?>(null)
    }


    var title by remember { mutableStateOf("") }

    var description by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var venue by remember {
        mutableStateOf("")
    }

    var showErrors by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // VALIDATION
    // ========================================================

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


    // ========================================================
    // UI
    // ========================================================


    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            title = ""
            description = ""
            category = ""
            date = ""
            time = ""
            venue = ""
            bannerUri = null

            snackbarHostState.showSnackbar("Event created successfully")

            viewModel.clearSuccess()

            onEventCreated()
        }
    }

    LaunchedEffect(uiState.error) {

        uiState.error?.let { message ->

            snackbarHostState.showSnackbar(message)

            viewModel.clearError()

        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->

                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF16A34A),
                    contentColor = Color.White
                )
            }
        },
        topBar = {




            TopAppBar(

                title = {

                    Text(
                        text = "Create Event"
                    )
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
            imageUrl = null,

            imageUri = bannerUri,

            onImageSelected = {
                bannerUri = it
            },

            onRemoveImage = {
                bannerUri = null
            },



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


            buttonText = "Publish Event",

            onSubmit = {

                showErrors = true

                if (isFormValid) {
                    viewModel.createEvent(
                        title = title,
                        description = description,
                        category = category,
                        date = date,
                        time = time,
                        venue = venue,
                        imageUri = bannerUri
                    )
                }
            }
        )
    }
}