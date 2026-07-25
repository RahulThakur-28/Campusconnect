package com.rahul.campusconnect.presentation.announcement.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.announcement.components.AnnouncementForm
import com.rahul.campusconnect.presentation.announcement.viewmodel.CreateAnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnouncementScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: CreateAnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }
    var selectedAttachmentUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish Announcement", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnnouncementForm(
                imagePickerState = imagePickerState,
                onImageSelected = { uri ->
                    imagePickerState = imagePickerState.copy(imageUri = uri, imageUrl = null)
                },
                onRemoveImage = {
                    imagePickerState = imagePickerState.copy(imageUri = null, imageUrl = null)
                },
                attachmentUri = selectedAttachmentUri,
                attachmentUrl = null,
                onAttachmentSelected = { uri -> selectedAttachmentUri = uri },
                onRemoveAttachment = { selectedAttachmentUri = null },
                onSubmit = { title, description, category, imageUri, attachmentUri ->
                    viewModel.createAnnouncement(
                        title = title,
                        description = description,
                        category = category,
                        imageUri = imageUri,
                        attachmentUri = attachmentUri
                    )
                },
                buttonText = "Publish Announcement",
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(padding)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}
