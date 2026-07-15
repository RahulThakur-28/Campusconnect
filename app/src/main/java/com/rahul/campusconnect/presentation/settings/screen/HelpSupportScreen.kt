package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.presentation.settings.components.SettingItem
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
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
        ) {
            SectionHeader(title = "FREQUENTLY ASKED QUESTIONS", actionText = null)
            
            FaqItem(
                question = "How do I verify my student status?",
                answer = "Go to Profile -> Edit Profile and upload your student ID. Our team will review it within 24 hours."
            )
            FaqItem(
                question = "Can I download notes for offline use?",
                answer = "Yes, click the download button on any note card to save it to your device's storage."
            )
            FaqItem(
                question = "How do I report a found item?",
                answer = "Navigate to the Lost & Found module and click the '+' button in the top right corner."
            )

            SectionHeader(title = "CONTACT US", actionText = null)
            SettingItem(
                title = "Email Support",
                subtitle = "support@campusconnect.com",
                icon = Icons.Outlined.Email,
                onClick = { /* TODO: Intent to Email */ }
            )
            SettingItem(
                title = "Report a Bug",
                subtitle = "Let us know what's broken",
                icon = Icons.Outlined.BugReport,
                onClick = { /* TODO */ }
            )
            SettingItem(
                title = "Chat Support",
                subtitle = "Coming soon",
                icon = Icons.Outlined.Chat,
                onClick = { /* Future */ },
                showChevron = false
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
