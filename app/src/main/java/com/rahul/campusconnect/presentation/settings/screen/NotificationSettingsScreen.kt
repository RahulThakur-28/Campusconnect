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
import com.rahul.campusconnect.presentation.settings.components.SettingSwitchItem
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            SectionHeader(title = "GENERAL", actionText = null)
            SettingSwitchItem(
                title = "Push Notifications",
                subtitle = "Enable or disable all push notifications",
                icon = Icons.Outlined.NotificationsActive,
                checked = uiState.pushNotificationsEnabled,
                onCheckedChange = viewModel::togglePushNotifications
            )
            SettingSwitchItem(
                title = "Email Notifications",
                subtitle = "Receive updates via your college email",
                icon = Icons.Outlined.Email,
                checked = uiState.emailNotificationsEnabled,
                onCheckedChange = viewModel::toggleEmailNotifications
            )

            SectionHeader(title = "MODULE SPECIFIC", actionText = null)
            SettingSwitchItem(
                title = "Announcements",
                icon = Icons.Outlined.Campaign,
                checked = uiState.announcementNotificationsEnabled,
                onCheckedChange = viewModel::toggleAnnouncementNotifications
            )
            SettingSwitchItem(
                title = "Events",
                icon = Icons.Outlined.Event,
                checked = uiState.eventNotificationsEnabled,
                onCheckedChange = viewModel::toggleEventNotifications
            )
            SettingSwitchItem(
                title = "Placements",
                icon = Icons.Outlined.WorkOutline,
                checked = uiState.placementNotificationsEnabled,
                onCheckedChange = viewModel::togglePlacementNotifications
            )
            SettingSwitchItem(
                title = "Study Notes",
                icon = Icons.Outlined.Description,
                checked = uiState.notesNotificationsEnabled,
                onCheckedChange = viewModel::toggleNotesNotifications
            )
            SettingSwitchItem(
                title = "Lost & Found",
                icon = Icons.Outlined.Search,
                checked = uiState.lostFoundNotificationsEnabled,
                onCheckedChange = viewModel::toggleLostFoundNotifications
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
