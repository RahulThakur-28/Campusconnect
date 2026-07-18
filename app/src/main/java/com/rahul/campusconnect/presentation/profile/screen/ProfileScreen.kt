package com.rahul.campusconnect.presentation.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.model.UserRole
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.profile.components.SettingsRow
import com.rahul.campusconnect.presentation.profile.components.StatCard
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onMyActivityClick: (String) -> Unit, // category: Notes, Events, etc.
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,

    onPrivacyPolicyClick: () -> Unit,
    onAboutClick :()->Unit,
    onHelpSupportClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    onNotificationSettingsClick:() -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red)
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
            // 1. Top Section - Profile Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clickable { onEditProfileClick() },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Photo",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "${user.role.name.replace("_", " ").lowercase().capitalize()} • ${user.department}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // 2. Statistics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Notes", user.stats.notesUploaded.toString(), Modifier.weight(1f))
                StatCard("Events", user.stats.eventsJoined.toString(), Modifier.weight(1f))
                StatCard("Applied", user.stats.placementApplications.toString(), Modifier.weight(1f))
                StatCard("Reports", user.stats.lostFoundReports.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Quick Actions
            SectionHeader(title = "My Activity", actionText = null)
            SettingsRow(
                icon = Icons.Outlined.Description,
                title = "My Notes",
                onClick = { onMyActivityClick("Notes") }
            )
            SettingsRow(
                icon = Icons.Outlined.Event,
                title = "My Events",
                onClick = { onMyActivityClick("Events") }
            )
            SettingsRow(
                icon = Icons.Outlined.WorkOutline,
                title = "My Placements",
                onClick = { onMyActivityClick("Placements") }
            )
            SettingsRow(
                icon = Icons.Outlined.Search,
                title = "My Lost & Found Posts",
                onClick = { onMyActivityClick("Lost & Found") }
            )

            Spacer(modifier = Modifier.height(16.dp))



            // 4. Account Settings
            SectionHeader(title = "Account Settings", actionText = null)
            SettingsRow(
                icon = Icons.Outlined.PersonOutline,
                title = "Edit Profile",
                onClick = onEditProfileClick
            )
            SettingsRow(
                icon = Icons.Outlined.Lock,
                title = "Change Password",
                onClick = { /* TODO */ }
            )
            SettingsRow(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                onClick =onNotificationSettingsClick
            )
            SettingsRow(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Support & About
            SectionHeader(title = "More", actionText = null)
            SettingsRow(
                icon = Icons.Outlined.PrivacyTip,
                title = "Privacy Policy",
                onClick = onPrivacyPolicyClick
            )
            SettingsRow(
                icon = Icons.Outlined.HelpOutline,
                title = "Help & Support",
                onClick =onHelpSupportClick
            )
            SettingsRow(
                icon = Icons.Outlined.Info,
                title = "About CampusConnect",
                onClick =onAboutClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
