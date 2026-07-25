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
import com.rahul.campusconnect.presentation.announcement.viewmodel.EditAnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAnnouncementScreen(
    announcementId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditAnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }
    var selectedAttachmentUri by remember { mutableStateOf<Uri?>(null) }
    var removeImage by remember { mutableStateOf(false) }
    var removeAttachment by remember { mutableStateOf(false) }

    LaunchedEffect(announcementId) {
        viewModel.loadAnnouncement(announcementId)
    }

    LaunchedEffect(uiState.announcement) {
        uiState.announcement?.let {
            imagePickerState = imagePickerState.copy(
                imageUrl = it.imageUrl,
                imageUri = null
            )
        }
    }

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
                title = { Text("Edit Announcement", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.announcement != null) {
                AnnouncementForm(
                    initialAnnouncement = uiState.announcement,
                    imagePickerState = imagePickerState,
                    onImageSelected = { uri ->
                        imagePickerState = imagePickerState.copy(imageUri = uri, imageUrl = null)
                        removeImage = false
                    },
                    onRemoveImage = {
                        imagePickerState = imagePickerState.copy(imageUri = null, imageUrl = null)
                        removeImage = true
                    },
                    attachmentUri = selectedAttachmentUri,
                    attachmentUrl = uiState.announcement?.attachmentUrl,
                    onAttachmentSelected = { uri -> 
                        selectedAttachmentUri = uri
                        removeAttachment = false
                    },
                    onRemoveAttachment = { 
                        selectedAttachmentUri = null
                        removeAttachment = true
                    },
                    onSubmit = { title, description, category, imageUri, attachmentUri ->
                        viewModel.updateAnnouncement(
                            title = title,
                            description = description,
                            category = category,
                            imageUri = imageUri,
                            attachmentUri = attachmentUri,
                            removeImage = removeImage,
                            removeAttachment = removeAttachment
                        )
                    },
                    buttonText = "Update Announcement",
                    isLoading = uiState.isSubmitting,
                    modifier = Modifier.padding(padding)
                )
            }

            if (uiState.isSubmitting) {
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
