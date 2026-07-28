package com.rahul.campusconnect.presentation.settings.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.presentation.settings.components.SettingItem
import com.rahul.campusconnect.presentation.settings.components.SettingsProfileCard
import com.rahul.campusconnect.presentation.settings.components.SettingsSection
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel

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
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
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
                .verticalScroll(scrollState)
                .padding(bottom = 48.dp)
        ) {
            // 1. PROFILE SECTION
            SettingsProfileCard(
                user = uiState.user,
                onEditClick = onEditProfileClick,
                modifier = Modifier.padding(24.dp)
            )

            // 2. ACCOUNT SECTION
            SettingsSection(title = "Account") {
                SettingItem(
                    title = "Personal Information",
                    subtitle = "Manage your name, department and year",
                    icon = Icons.Rounded.Person,
                    onClick = onEditProfileClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Security",
                    subtitle = "Change password and account safety",
                    icon = Icons.Rounded.Security,
                    onClick = onChangePasswordClick
                )
            }

            // 3. PREFERENCES SECTION
            SettingsSection(title = "Preferences") {
                SettingItem(
                    title = "Appearance",
                    subtitle = when(uiState.theme) {
                        AppTheme.SYSTEM -> "System Default"
                        AppTheme.LIGHT -> "Light Mode"
                        AppTheme.DARK -> "Dark Mode"
                    },
                    icon = Icons.Rounded.Palette,
                    onClick = { showThemeDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Notifications",
                    subtitle = "Manage your campus alerts",
                    icon = Icons.Rounded.Notifications,
                    onClick = onNotificationSettingsClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "App Language",
                    subtitle = "English (United States)",
                    icon = Icons.Rounded.Language,
                    onClick = { showLanguageDialog = true }
                )
            }

            // 4. SUPPORT & LEGAL SECTION
            SettingsSection(title = "Support & Legal") {
                SettingItem(
                    title = "Help & Support",
                    subtitle = "FAQs and contact support",
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    onClick = onHelpSupportClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Privacy Policy",
                    icon = Icons.Rounded.PrivacyTip,
                    onClick = onPrivacyPolicyClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Terms & Conditions",
                    icon = Icons.Rounded.Gavel,
                    onClick = onTermsClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Report a Bug",
                    icon = Icons.Rounded.BugReport,
                    onClick = onBugReportClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "About CampusConnect",
                    icon = Icons.Rounded.Info,
                    onClick = onAboutClick
                )
            }

            // 5. DANGER ZONE SECTION
            SettingsSection(title = "Account Actions") {
                SettingItem(
                    title = "Logout",
                    icon = Icons.AutoMirrored.Rounded.Logout,
                    onClick = { showLogoutDialog = true },
                    contentColor = MaterialTheme.colorScheme.error
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingItem(
                    title = "Delete Account",
                    subtitle = "Permanently delete your profile and data",
                    icon = Icons.Rounded.DeleteForever,
                    onClick = onDeleteAccountClick,
                    contentColor = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "CampusConnect v${uiState.appVersion}\nBuild ${uiState.buildNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onLogout = {
                viewModel.logout()
                showLogoutDialog = false
                onLogoutSuccess()
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionSheet(
            currentTheme = uiState.theme,
            onThemeSelected = {
                viewModel.setTheme(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionSheet(
            currentLanguage = "en",
            onLanguageSelected = { showLanguageDialog = false },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Logout, 
                        null, 
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = { 
            Text(
                "Sign Out?", 
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            ) 
        },
        text = { 
            Text(
                "You are about to sign out of your account. You'll need to enter your credentials again to access your data.",
                textAlign = TextAlign.Center
            ) 
        },
        confirmButton = {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Stay Logged In", fontWeight = FontWeight.SemiBold)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionSheet(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                "App Appearance",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            SelectionRadioCard(
                label = "System Default",
                description = "Follow device settings",
                isSelected = currentTheme == AppTheme.SYSTEM,
                icon = Icons.Rounded.SettingsSuggest,
                onClick = { onThemeSelected(AppTheme.SYSTEM) }
            )
            Spacer(Modifier.height(12.dp))
            SelectionRadioCard(
                label = "Light Mode",
                description = "Classic clear appearance",
                isSelected = currentTheme == AppTheme.LIGHT,
                icon = Icons.Rounded.LightMode,
                onClick = { onThemeSelected(AppTheme.LIGHT) }
            )
            Spacer(Modifier.height(12.dp))
            SelectionRadioCard(
                label = "Dark Mode",
                description = "Easy on the eyes in the dark",
                isSelected = currentTheme == AppTheme.DARK,
                icon = Icons.Rounded.DarkMode,
                onClick = { onThemeSelected(AppTheme.DARK) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionSheet(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                "Select Language",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            SelectionRadioCard(
                label = "English",
                description = "English",
                isSelected = currentLanguage == "en",
                icon = Icons.Rounded.Language,
                onClick = { onLanguageSelected("en") }
            )
            Spacer(Modifier.height(12.dp))
            SelectionRadioCard(
                label = "हिन्दी",
                description = "Hindi",
                isSelected = currentLanguage == "hi",
                icon = Icons.Rounded.Translate,
                onClick = { onLanguageSelected("hi") }
            )
        }
    }
}

@Composable
fun SelectionRadioCard(
    label: String,
    description: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(selected = isSelected, onClick = onClick)
        }
    }
}
