package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.presentation.event.components.RegisterButton
import com.rahul.campusconnect.presentation.event.viewmodel.EventDetailsViewModel
import com.rahul.campusconnect.ui.components.CardImageHeader
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onViewDiscussionClick: () -> Unit,
    navController: NavController,
    viewModel: EventDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetDeleteState()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Event") },
            text = { Text("Are you sure you want to delete this event? This will mark it as deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEvent()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.canEdit) {
                        IconButton(onClick = { onEditClick(eventId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                    IconButton(onClick = {
                        val sendIntent: android.content.Intent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "Check out this event: ${uiState.event?.title}\nVenue: ${uiState.event?.venue}\nDate: ${TimeUtils.formatDate(uiState.event?.startDate ?: 0L)}")
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.event != null) {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        RegisterButton(
                            isRegistered = uiState.isRegistered,
                            onClick = {
                                if (uiState.isRegistered) viewModel.unregisterFromEvent()
                                else viewModel.registerForEvent()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.event!!.isRegistrationOpen && (uiState.event!!.registeredCount < uiState.event!!.maxParticipants || uiState.event!!.maxParticipants == 0)
                        )
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.event == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                EmptyState(
                    message = uiState.error ?: "Error loading event",
                    buttonText = "Retry",
                    onButtonClick = { viewModel.loadEvent(eventId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.event != null -> {
                val event = uiState.event!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    CardImageHeader(
                        imageUrl = event.imageUrl,
                        category = event.category,
                        categoryColor = MaterialTheme.colorScheme.primary,
                        height = 240.dp
                    )

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Organizer
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = event.organizerName, fontWeight = FontWeight.Bold)
                                Text(text = event.organizerRole, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        InfoRow(icon = Icons.Default.CalendarToday, text = TimeUtils.formatDate(event.startDate))
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(icon = Icons.Default.Schedule, text = event.time)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(icon = Icons.Default.LocationOn, text = event.venue)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(
                            icon = Icons.Default.Groups, 
                            text = "${event.registeredCount}${if (event.maxParticipants > 0) " / ${event.maxParticipants}" else ""} Participants"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(icon = Icons.Default.History, text = "Posted ${TimeUtils.getRelativeTime(event.createdAt)}")

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(text = "About Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Discussion Preview
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(text = "Questions & Answers", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(
                                    onClick = onViewDiscussionClick,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(text = "View Discussion →")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
