package com.rahul.campusconnect.presentation.onboarding


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import com.rahul.campusconnect.R

val onboardingPages = listOf(

    OnboardingPage(
        imageRes = R.drawable.onboarding_connect,
        icon = Icons.Default.People,
        title = "Connect with Campus",
        description = "Meet students, join study groups, and build meaningful connections."
    ),

    OnboardingPage(
        imageRes = R.drawable.second_on_boarding,
        icon = Icons.Default.School,
        title = "Learn Together",
        description = "Share notes, collaborate on assignments and grow with your classmates."
    ),

    OnboardingPage(
        imageRes = R.drawable.event,
        icon = Icons.Default.Event,
        title = "Stay Updated",
        description = "Never miss college events, announcements and exciting opportunities."
    )

)