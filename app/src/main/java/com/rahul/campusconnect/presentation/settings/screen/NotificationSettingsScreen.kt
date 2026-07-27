package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.repository.NotificationType
import com.rahul.campusconnect.presentation.settings.components.SettingSwitchItem
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

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
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
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
            SectionHeader(title = "MODULE ALERTS", actionText = null)
            
            SettingSwitchItem(
                title = "Announcements",
                icon = Icons.Outlined.Campaign,
                checked = prefs.announcements,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.ANNOUNCEMENTS, it) }
            )
            SettingSwitchItem(
                title = "Events",
                icon = Icons.Outlined.Event,
                checked = prefs.events,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.EVENTS, it) }
            )
            SettingSwitchItem(
                title = "Placements",
                icon = Icons.Outlined.WorkOutline,
                checked = prefs.placements,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.PLACEMENTS, it) }
            )
            SettingSwitchItem(
                title = "Study Notes",
                icon = Icons.Outlined.Description,
                checked = prefs.notes,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.NOTES, it) }
            )
            SettingSwitchItem(
                title = "Lost & Found",
                icon = Icons.Outlined.Search,
                checked = prefs.lostFound,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.LOST_FOUND, it) }
            )
            SettingSwitchItem(
                title = "Discussion Replies",
                icon = Icons.Outlined.QuestionAnswer,
                checked = prefs.discussionReplies,
                onCheckedChange = { viewModel.updateNotificationPreference(NotificationType.DISCUSSION_REPLIES, it) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
