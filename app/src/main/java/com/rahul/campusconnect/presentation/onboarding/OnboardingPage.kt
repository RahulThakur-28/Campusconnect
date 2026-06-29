package com.rahul.campusconnect.presentation.onboarding


import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage(
    val imageRes: Int,
    val icon: ImageVector,
    val title: String,
    val description: String
)