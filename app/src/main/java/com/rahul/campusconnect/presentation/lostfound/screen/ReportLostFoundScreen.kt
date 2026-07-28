package com.rahul.campusconnect.presentation.lostfound.screen

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
import com.rahul.campusconnect.presentation.lostfound.components.LostFoundForm
import com.rahul.campusconnect.presentation.lostfound.viewmodel.CreateLostFoundViewModel
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostFoundScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: CreateLostFoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var imagePickerState by remember { mutableStateOf(ImagePickerState()) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Item reported successfully")
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
                title = { Text("Submit Report", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LostFoundForm(
                imagePickerState = imagePickerState,
                onImageSelected = { uri ->
                    imagePickerState = imagePickerState.copy(imageUri = uri, imageUrl = null)
                },
                onRemoveImage = {
                    imagePickerState = imagePickerState.copy(imageUri = null, imageUrl = null)
                },
                onSubmit = { title, description, category, type, location, email, phone ->
                    viewModel.createReport(
                        title = title,
                        description = description,
                        category = category,
                        type = type,
                        location = location,
                        contactEmail = email,
                        contactPhone = phone,
                        imageUri = imagePickerState.imageUri
                    )
                },
                buttonText = "Submit Report",
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
