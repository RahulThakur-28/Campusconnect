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
import com.rahul.campusconnect.presentation.event.viewmodel.CreateEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: CreateEventViewModel = hiltViewModel()
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

    var showErrors by remember { mutableStateOf(false) }

    // Validation
    val titleError = if (showErrors && title.isBlank()) "Title is required" else null
    val descriptionError = if (showErrors && description.length < 10) "Description must be at least 10 chars" else null
    val categoryError = if (showErrors && category.isBlank()) "Please select a category" else null
    val dateError = if (showErrors && startDate == 0L) "Please select a date" else null
    val timeError = if (showErrors && time.isBlank()) "Please select a time" else null
    val venueError = if (showErrors && venue.isBlank()) "Venue is required" else null

    val isFormValid = title.isNotBlank() && description.length >= 10 && category.isNotBlank() && startDate != 0L && time.isNotBlank() && venue.isNotBlank()

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
                title = { Text("Create Event", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                buttonText = "Publish Event",
                isLoading = uiState.isLoading,
                imageUri = bannerUri,
                onImageSelected = { bannerUri = it },
                onRemoveImage = { bannerUri = null },
                onSubmit = {
                    if (isFormValid) {
                        viewModel.createEvent(
                            title = title,
                            description = description,
                            category = category,
                            startDate = startDate,
                            endDate = startDate, // Default same as start
                            time = time,
                            venue = venue,
                            maxParticipants = maxParticipants.toIntOrNull() ?: 0,
                            isRegistrationOpen = isRegistrationOpen,
                            imageUri = bannerUri
                        )
                    } else {
                        showErrors = true
                    }
                }
            )
        }
    }
}
