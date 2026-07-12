package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rahul.campusconnect.presentation.placement.components.PlacementForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlacementScreen(
    onBackClick: () -> Unit,
    onPlacementCreated: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Placement Drive", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        PlacementForm(
            onSubmit = { 
                // Handle creation via ViewModel
                onPlacementCreated() 
            },
            buttonText = "Publish Drive",
            modifier = Modifier.padding(padding)
        )
    }
}
