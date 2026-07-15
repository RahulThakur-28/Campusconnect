package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.presentation.settings.components.SettingItem
import com.rahul.campusconnect.presentation.settings.components.SettingSwitchItem
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
            // ACCOUNT
            SectionHeader(title = "ACCOUNT", actionText = null)
            SettingItem(
                title = "Edit Profile",
                icon = Icons.Outlined.Person,
                onClick = onEditProfileClick
            )
            SettingItem(
                title = "Change Password",
                icon = Icons.Outlined.Lock,
                onClick = { /* TODO */ }
            )

            // NOTIFICATIONS
            SectionHeader(title = "NOTIFICATIONS", actionText = null)
            SettingItem(
                title = "Notification Preferences",
                subtitle = "Manage what you get notified about",
                icon = Icons.Outlined.Notifications,
                onClick = onNotificationSettingsClick
            )

            // APP PREFERENCES
            SectionHeader(title = "APP PREFERENCES", actionText = null)
            SettingSwitchItem(
                title = "Dark Mode",
                icon = Icons.Outlined.DarkMode,
                checked = uiState.isDarkMode,
                onCheckedChange = viewModel::toggleDarkMode
            )
            SettingItem(
                title = "Language",
                subtitle = "English (US)",
                icon = Icons.Outlined.Language,
                onClick = { /* Future */ }
            )

            // SUPPORT
            SectionHeader(title = "SUPPORT", actionText = null)
            SettingItem(
                title = "Help & Support",
                icon = Icons.Outlined.HelpOutline,
                onClick = onHelpSupportClick
            )
            SettingItem(
                title = "Report a Bug",
                icon = Icons.Outlined.BugReport,
                onClick = { /* TODO */ }
            )
            SettingItem(
                title = "Contact Developer",
                icon = Icons.Outlined.Email,
                onClick = { /* TODO */ }
            )

            // LEGAL
            SectionHeader(title = "LEGAL", actionText = null)
            SettingItem(
                title = "Privacy Policy",
                icon = Icons.Outlined.PrivacyTip,
                onClick = onPrivacyPolicyClick
            )
            SettingItem(
                title = "Terms & Conditions",
                icon = Icons.Outlined.Gavel,
                onClick = onTermsClick
            )

            // ABOUT
            SectionHeader(title = "ABOUT", actionText = null)
            SettingItem(
                title = "About CampusConnect",
                icon = Icons.Outlined.Info,
                onClick = onAboutClick
            )
            SettingItem(
                title = "App Version",
                subtitle = uiState.appVersion,
                icon = Icons.Outlined.Code,
                showChevron = false
            )

            // ACCOUNT ACTIONS
            SectionHeader(title = "ACCOUNT ACTIONS", actionText = null)
            
            Box(modifier = Modifier.padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Logout", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                    ) {
                        Icon(Icons.Outlined.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete Account", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.logout()
                    showLogoutDialog = false 
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteAccount()
                    showDeleteDialog = false 
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
