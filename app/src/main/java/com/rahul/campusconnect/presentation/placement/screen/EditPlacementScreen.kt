package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.placement.components.PlacementForm
import com.rahul.campusconnect.presentation.placement.viewmodel.EditPlacementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlacementScreen(
    placementId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditPlacementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }
    var removeLogo by remember { mutableStateOf(false) }

    LaunchedEffect(placementId) {
        viewModel.loadPlacement(placementId)
    }

    LaunchedEffect(uiState.placement) {
        uiState.placement?.let { placement ->
            imagePickerState = imagePickerState.copy(
                imageUrl = placement.logoUrl,
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
                title = { Text(text = "Edit Placement Drive", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.placement != null -> {
                    PlacementForm(
                        modifier = Modifier.padding(padding),
                        initialPlacement = uiState.placement,
                        imagePickerState = imagePickerState,
                        onImageSelected = { uri ->
                            imagePickerState = imagePickerState.copy(imageUri = uri, imageUrl = null)
                            removeLogo = false
                        },
                        onRemoveImage = {
                            imagePickerState = imagePickerState.copy(imageUri = null, imageUrl = null)
                            removeLogo = true
                        },
                        buttonText = "Update Drive",
                        onSubmit = { updatedPlacement, attachmentUri, removeAttachment ->
                            viewModel.updatePlacement(
                                placement = updatedPlacement,
                                logoUri = imagePickerState.imageUri,
                                attachmentUri = attachmentUri,
                                removeLogo = removeLogo,
                                removeAttachment = removeAttachment
                            )
                        }
                    )
                }
            }

            if (uiState.isSubmitting) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            },
            title = { Text("Error") },
            text = { Text(error) }
        )
    }
}
