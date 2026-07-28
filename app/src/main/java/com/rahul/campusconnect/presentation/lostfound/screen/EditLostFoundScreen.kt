package com.rahul.campusconnect.presentation.lostfound.screen

import androidx.compose.foundation.layout.*
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
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.lostfound.components.LostFoundForm
import com.rahul.campusconnect.presentation.lostfound.viewmodel.EditLostFoundViewModel
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLostFoundScreen(
    itemId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditLostFoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Report updated successfully")
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val msg = data.visuals.message.lowercase()
                val containerColor = if (msg.contains("success")) SuccessGreen else MaterialTheme.colorScheme.error
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
                title = { Text("Edit Report", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.item == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
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
