package com.rahul.campusconnect.presentation.announcement.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.announcement.components.AnnouncementForm
import com.rahul.campusconnect.presentation.announcement.viewmodel.EditAnnouncementViewModel
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAnnouncementScreen(
    announcementId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditAnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Announcement updated successfully")
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val msg = data.visuals.message.lowercase()
                val containerColor = when {
                    msg.contains("success") || msg.contains("successfully") -> SuccessGreen
                    msg.contains("warning") -> Color(0xFFF2994A) // Orange
                    msg.contains("info") -> Color(0xFF2D9CDB) // Blue
                    else -> MaterialTheme.colorScheme.error
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = containerColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Edit Announcement", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.announcement == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
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
                    buttonText = "Save Changes",
                    isLoading = uiState.isSubmitting,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
}
