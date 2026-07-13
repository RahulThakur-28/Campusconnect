package com.rahul.campusconnect.presentation.placement.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rahul.campusconnect.model.Placement
import com.rahul.campusconnect.presentation.placement.components.PlacementForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlacementScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onPlacementUpdated: () -> Unit
) {
    // Dummy - fetch by ID in real app
    val placement = Placement(
        id = placementId,
        companyName = "Google",
        role = "Software Engineer",
        packageAmount = "45 lp",
        location = "Bangalore",
        jobType = "Full-time",
        openings = 10,
        deadline = "30 Oct 2024",
        applyLink = "https://google.com/careers",
        eligibility = "7.5 CGPA+",
        category = "IT",
        description = "Join Google's dynamic engineering team.",
        requiredSkills = listOf("Kotlin", "Java"),
        applicationProcess = "Online Test -> Interview"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Placement Drive", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        PlacementForm(
            initialPlacement = placement,
            onSubmit = { 
                // Handle update via ViewModel
                onPlacementUpdated() 
            },
            buttonText = "Update Drive",
            modifier = Modifier.padding(padding)
        )
    }
}
