package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.placement.components.PlacementForm
import com.rahul.campusconnect.presentation.placement.viewmodel.CreatePlacementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlacementScreen(
    onBackClick: () -> Unit,
    onPlacementCreated: () -> Unit,
    viewModel: CreatePlacementViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    var imagePickerState by remember {
        mutableStateOf(ImagePickerState())
    }
    onPlacementCreated()
    viewModel.resetSuccessState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onPlacementCreated()
            viewModel.resetSuccessState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Placement Drive",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        PlacementForm(

            modifier = Modifier.padding(padding),

            imagePickerState = imagePickerState,

            onImageSelected = { uri ->
                imagePickerState = imagePickerState.copy(
                    imageUri = uri,
                    imageUrl = null
                )
            },

            onRemoveImage = {
                imagePickerState = imagePickerState.copy(
                    imageUri = null,
                    imageUrl = null
                )
            },

            buttonText = "Create Drive",

            onSubmit = { placement ->

                viewModel.createPlacement(
                    placement = placement,
                    logoUri = imagePickerState.imageUri
                )
            }
        )
    }

    if (uiState.isSubmitting) {

        AlertDialog(

            onDismissRequest = {},

            confirmButton = {},

            title = {
                Text("Publishing Placement")
            },

            text = {
                CircularProgressIndicator()
            }
        )
    }

    uiState.error?.let { error ->

        AlertDialog(

            onDismissRequest = {
                viewModel.clearError()
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        viewModel.clearError()
                    }
                ) {
                    Text("OK")
                }
            },

            title = {
                Text("Error")
            },

            text = {
                Text(error)
            }
        )
    }
}