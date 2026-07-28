package com.rahul.campusconnect.presentation.event.screen

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val snackbarHostState = remember { SnackbarHostState() }

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

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    // Validation
    val titleError = if (showErrors && title.isBlank()) "Title is required" else null
    val descriptionError = if (showErrors && description.length < 10) "Description must be at least 10 chars" else null
    val categoryError = if (showErrors && category.isBlank()) "Please select a category" else null
    val dateError = if (showErrors && startDate == 0L) "Please select a date" else null
    val timeError = if (showErrors && time.isBlank()) "Please select a time" else null
    val venueError = if (showErrors && venue.isBlank()) "Venue is required" else null

    val isFormValid = title.isNotBlank() && description.length >= 10 && category.isNotBlank() && startDate != 0L && time.isNotBlank() && venue.isNotBlank()

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val isSuccess = data.visuals.message.contains("Successfully", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Edit Event", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.event == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), strokeWidth = 3.dp)
            } else if (uiState.event != null) {
                EventForm(
                    modifier = Modifier.padding(padding),
                    title = title,
                    onTitleChange = { title = it; if(showErrors) showErrors = false },
                    titleError = titleError,
                    description = description,
                    onDescriptionChange = { description = it; if(showErrors) showErrors = false },
                    descriptionError = descriptionError,
                    category = category,
                    onCategoryChange = { category = it; if(showErrors) showErrors = false },
                    categoryError = categoryError,
                    startDate = startDate,
                    onStartDateChange = { startDate = it; if(showErrors) showErrors = false },
                    dateError = dateError,
                    time = time,
                    onTimeChange = { time = it; if(showErrors) showErrors = false },
                    timeError = timeError,
                    venue = venue,
                    onVenueChange = { venue = it; if(showErrors) showErrors = false },
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
                        } else {
                            showErrors = true
                        }
                    }
                )
            }
        }
    }
}
