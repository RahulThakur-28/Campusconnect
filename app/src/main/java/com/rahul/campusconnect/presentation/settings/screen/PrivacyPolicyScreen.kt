package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text(
                text = "Last Updated: July 2024",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            PolicySection(
                title = "1. Information We Collect",
                content = "We collect information you provide directly to us when you create an account, such as your name, college email, student ID, and department. We also collect content you post, such as notes, announcements, and lost & found reports."
            )

            PolicySection(
                title = "2. How We Use Your Information",
                content = "We use the information we collect to provide, maintain, and improve CampusConnect. This includes facilitating connections between students, providing personalized content, and sending notifications about relevant campus activities."
            )

            PolicySection(
                title = "3. Sharing of Information",
                content = "Your profile information (name, role, department) is visible to other registered users of CampusConnect. We do not sell your personal data to third parties."
            )

            PolicySection(
                title = "4. Security",
                content = "We take reasonable measures to help protect information about you from loss, theft, misuse and unauthorized access."
            )

            PolicySection(
                title = "5. Your Choices",
                content = "You may update your account information at any time by logging into your account and visiting your profile settings. You can also delete your account through the settings menu."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
