package com.rahul.campusconnect.presentation.settings.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.presentation.settings.components.SettingItem
import com.rahul.campusconnect.presentation.settings.components.SettingsProfileCard
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

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
                .padding(bottom = 32.dp)
        ) {
            // PROFILE CARD
            SettingsProfileCard(
                user = uiState.user,
                onEditClick = onEditProfileClick,
                modifier = Modifier.padding(16.dp)
            )

            // ACCOUNT SECTION
            SectionHeader(title = "ACCOUNT", actionText = null)
            SettingItem(
                title = "Edit Profile",
                icon = Icons.Outlined.Person,
                onClick = onEditProfileClick
            )
            SettingItem(
                title = "Change Password",
                icon = Icons.Outlined.Lock,
                onClick = onChangePasswordClick
            )
            SettingItem(
                title = "Logout",
                icon = Icons.Outlined.Logout,
                onClick = { showLogoutDialog = true },
                contentColor = MaterialTheme.colorScheme.error
            )

            // PREFERENCES SECTION
            SectionHeader(title = "PREFERENCES", actionText = null)
            SettingItem(
                title = "Dark Mode",
                subtitle = when(uiState.theme) {
                    AppTheme.SYSTEM -> "System Default"
                    AppTheme.LIGHT -> "Light"
                    AppTheme.DARK -> "Dark"
                },
                icon = Icons.Outlined.DarkMode,
                onClick = { showThemeDialog = true }
            )
            SettingItem(
                title = "Notifications",
                subtitle = "Manage campus alerts",
                icon = Icons.Outlined.Notifications,
                onClick = onNotificationSettingsClick
            )
            SettingItem(
                title = "Language",
                subtitle = "English",
                icon = Icons.Outlined.Language,
                onClick = { /* Future ready */ }
            )

            // SECURITY SECTION
            SectionHeader(title = "SECURITY", actionText = null)
            SettingItem(
                title = "Active Session",
                subtitle = "${uiState.deviceInfo}\nLast login: ${uiState.loginTime}",
                icon = Icons.Outlined.Security,
                onClick = { /* Info only */ },
                showChevron = false
            )
            SettingItem(
                title = "Account Security",
                subtitle = "Provider: ${uiState.authProvider}",
                icon = Icons.Outlined.VerifiedUser,
                onClick = { /* Info only */ },
                showChevron = false
            )
            SettingItem(
                title = "Delete Account",
                icon = Icons.Outlined.DeleteForever,
                onClick = onDeleteAccountClick,
                contentColor = MaterialTheme.colorScheme.error
            )

            // SUPPORT SECTION
            SectionHeader(title = "SUPPORT & ABOUT", actionText = null)
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
            SettingItem(
                title = "Contact Us",
                icon = Icons.Outlined.Email,
                onClick = { openEmail(context) }
            )
            SettingItem(
                title = "Report a Bug",
                icon = Icons.Outlined.BugReport,
                onClick = onBugReportClick
            )
            SettingItem(
                title = "About CampusConnect",
                icon = Icons.Outlined.Info,
                onClick = onAboutClick
            )
            SettingItem(
                title = "Rate App",
                icon = Icons.Outlined.StarRate,
                onClick = { openPlayStore(context) }
            )
            SettingItem(
                title = "Share App",
                icon = Icons.Outlined.Share,
                onClick = { shareApp(context) }
            )
            SettingItem(
                title = "App Version",
                subtitle = "${uiState.appVersion} (${uiState.buildNumber})",
                icon = Icons.Outlined.Code,
                showChevron = false
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout? You will need to sign in again to access your account.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.logout()
                    showLogoutDialog = false
                    onLogoutSuccess()
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

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    ThemeOption("System Default", uiState.theme == AppTheme.SYSTEM) {
                        viewModel.setTheme(AppTheme.SYSTEM)
                        showThemeDialog = false
                    }
                    ThemeOption("Light", uiState.theme == AppTheme.LIGHT) {
                        viewModel.setTheme(AppTheme.LIGHT)
                        showThemeDialog = false
                    }
                    ThemeOption("Dark", uiState.theme == AppTheme.DARK) {
                        viewModel.setTheme(AppTheme.DARK)
                        showThemeDialog = false
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun ThemeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun openEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:support@campusconnect.com")
        putExtra(Intent.EXTRA_SUBJECT, "CampusConnect Support Request")
    }
    context.startActivity(intent)
}

private fun openPlayStore(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("market://details?id=${context.packageName}")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
    }
}

private fun shareApp(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "CampusConnect")
        putExtra(Intent.EXTRA_TEXT, "Hey! Check out CampusConnect, the ultimate campus companion: https://play.google.com/store/apps/details?id=${context.packageName}")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}
