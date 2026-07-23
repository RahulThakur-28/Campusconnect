package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.presentation.placement.components.PlacementForm
import com.rahul.campusconnect.presentation.placement.viewmodel.EditPlacementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlacementScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onPlacementUpdated: () -> Unit,
    viewModel: EditPlacementViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    var imagePickerState by remember {
        mutableStateOf(ImagePickerState())
    }

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

            onPlacementUpdated()

            viewModel.resetSuccessState()
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Edit Placement Drive",
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

        when {

            uiState.isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            uiState.placement != null -> {

                PlacementForm(

                    modifier = Modifier.padding(padding),

                    initialPlacement = uiState.placement,

                    imagePickerState = imagePickerState,

                    onImageSelected = { uri ->

                        imagePickerState = imagePickerState.copy(
                            imageUri = uri
                        )
                    },

                    onRemoveImage = {

                        imagePickerState = imagePickerState.copy(
                            imageUri = null,
                            imageUrl = null
                        )
                    },

                    buttonText = "Update Drive",

                    onSubmit = { updatedPlacement ->

                        viewModel.updatePlacement(
                            placement = updatedPlacement,
                            logoUri = imagePickerState.imageUri
                        )
                    }
                )
            }
        }
    }

    uiState.error?.let { message ->

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
                Text(message)
            }
        )
    }
}