package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.repository.NotificationType
import com.rahul.campusconnect.presentation.settings.components.SettingSwitchItem
import com.rahul.campusconnect.presentation.settings.components.SettingsSection
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val prefs = uiState.notificationPreferences

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Notifications", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Choose which campus updates you want to receive. You can change these anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Campus Alerts") {
                SettingSwitchItem(
                    title = "Announcements",
                    subtitle = "Get notified about important college notices",
                    icon = Icons.Rounded.Campaign,
                    checked = prefs.announcements,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.ANNOUNCEMENTS, it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingSwitchItem(
                    title = "Events",
                    subtitle = "Updates on upcoming workshops and fests",
                    icon = Icons.Rounded.Event,
                    checked = prefs.events,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.EVENTS, it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingSwitchItem(
                    title = "Placements",
                    subtitle = "New job opportunities and drive alerts",
                    icon = Icons.Rounded.BusinessCenter,
                    checked = prefs.placements,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.PLACEMENTS, it) }
                )
            }

            SettingsSection(title = "Community") {
                SettingSwitchItem(
                    title = "Study Notes",
                    subtitle = "When new materials are uploaded",
                    icon = Icons.Rounded.Description,
                    checked = prefs.notes,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.NOTES, it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingSwitchItem(
                    title = "Lost & Found",
                    subtitle = "Alerts for reported items near you",
                    icon = Icons.Rounded.Search,
                    checked = prefs.lostFound,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.LOST_FOUND, it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingSwitchItem(
                    title = "Discussion Replies",
                    subtitle = "When someone replies to your questions",
                    icon = Icons.Rounded.QuestionAnswer,
                    checked = prefs.discussionReplies,
                    onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.DISCUSSION_REPLIES, it) }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
