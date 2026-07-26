package com.rahul.campusconnect.presentation.event.screen

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.presentation.event.components.EventForm
import com.rahul.campusconnect.presentation.event.viewmodel.EditEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditEventViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(0L) }
    var time by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("0") }
    var isRegistrationOpen by remember { mutableStateOf(true) }
    var removeImage by remember { mutableStateOf(false) }

    var showErrors by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(uiState.event) {
        uiState.event?.let { event ->
            title = event.title
            description = event.description
            category = event.category
            startDate = event.startDate
            time = event.time
            venue = event.venue
            maxParticipants = event.maxParticipants.toString()
            isRegistrationOpen = event.isRegistrationOpen
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

    // Validation
    val titleError = if (showErrors && title.isBlank()) "Title is required" else null
    val descriptionError = if (showErrors && description.length < 10) "Description too short" else null
    val categoryError = if (showErrors && category.isBlank()) "Category is required" else null
    val dateError = if (showErrors && startDate == 0L) "Date is required" else null
    val timeError = if (showErrors && time.isBlank()) "Time is required" else null
    val venueError = if (showErrors && venue.isBlank()) "Venue is required" else null

    val isFormValid = title.isNotBlank() && description.length >= 10 && category.isNotBlank() && startDate != 0L && time.isNotBlank() && venue.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.event == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.event != null) {
                EventForm(
                    modifier = Modifier.padding(padding),
                    title = title,
                    onTitleChange = { title = it },
                    titleError = titleError,
                    description = description,
                    onDescriptionChange = { description = it },
                    descriptionError = descriptionError,
                    category = category,
                    onCategoryChange = { category = it },
                    categoryError = categoryError,
                    startDate = startDate,
                    onStartDateChange = { startDate = it },
                    dateError = dateError,
                    time = time,
                    onTimeChange = { time = it },
                    timeError = timeError,
                    venue = venue,
                    onVenueChange = { venue = it },
                    venueError = venueError,
                    maxParticipants = maxParticipants,
                    onMaxParticipantsChange = { if (it.all { char -> char.isDigit() }) maxParticipants = it },
                    isRegistrationOpen = isRegistrationOpen,
                    onRegistrationOpenChange = { isRegistrationOpen = it },
                    buttonText = "Update Event",
                    isLoading = uiState.isLoading,
                    imageUrl = uiState.event?.imageUrl,
                    imageUri = bannerUri,
                    onImageSelected = { 
                        bannerUri = it
                        removeImage = false
                    },
                    onRemoveImage = { 
                        bannerUri = null
                        removeImage = true
                    },
                    onSubmit = {
                        showErrors = true
                        if (isFormValid) {
                            viewModel.updateEvent(
                                title = title,
                                description = description,
                                category = category,
                                startDate = startDate,
                                endDate = startDate,
                                time = time,
                                venue = venue,
                                maxParticipants = maxParticipants.toIntOrNull() ?: 0,
                                isRegistrationOpen = isRegistrationOpen,
                                imageUri = bannerUri,
                                removeImage = removeImage
                            )
                        }
                    }
                )
            }

            if (uiState.isLoading && uiState.event != null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }
}
