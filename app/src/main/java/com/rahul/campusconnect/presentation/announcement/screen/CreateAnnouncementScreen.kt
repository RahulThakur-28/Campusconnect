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
import com.rahul.campusconnect.presentation.announcement.viewmodel.CreateAnnouncementViewModel
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnouncementScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: CreateAnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }
    var selectedAttachmentUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Announcement published successfully")
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
                title = { Text("Publish Announcement", fontWeight = FontWeight.ExtraBold) },
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
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
}
