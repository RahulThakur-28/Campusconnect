package com.rahul.campusconnect.presentation.lostfound.screen

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
import com.rahul.campusconnect.presentation.lostfound.components.LostFoundForm
import com.rahul.campusconnect.presentation.lostfound.viewmodel.EditLostFoundViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLostFoundScreen(
    itemId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditLostFoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }
    var removeImage by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    LaunchedEffect(uiState.item) {
        uiState.item?.let {
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
                title = { Text("Edit Item Report", fontWeight = FontWeight.Bold) },
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
            } else if (uiState.item != null) {
                LostFoundForm(
                    initialItem = uiState.item,
                    imagePickerState = imagePickerState,
                    onImageSelected = { uri ->
                        imagePickerState = imagePickerState.copy(imageUri = uri, imageUrl = null)
                        removeImage = false
                    },
                    onRemoveImage = {
                        imagePickerState = imagePickerState.copy(imageUri = null, imageUrl = null)
                        removeImage = true
                    },
                    onSubmit = { title, description, category, type, location, email, phone ->
                        viewModel.updateItem(
                            title = title,
                            description = description,
                            category = category,
                            type = type,
                            location = location,
                            contactEmail = email,
                            contactPhone = phone,
                            newImageUri = imagePickerState.imageUri,
                            removeImage = removeImage
                        )
                    },
                    buttonText = "Update Report",
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
                    CircularProgressIndicator()
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
