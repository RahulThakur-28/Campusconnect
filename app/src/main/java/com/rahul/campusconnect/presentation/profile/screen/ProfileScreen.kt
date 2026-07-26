package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
import com.rahul.campusconnect.presentation.profile.components.SettingsRow
import com.rahul.campusconnect.presentation.profile.components.StatCard
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onMyActivityClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user = uiState.user
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    if (uiState.isLoading && user.uid.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogoutClick()
                    }) {
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
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        if (user.profileImage.isNotBlank()) {
                            AsyncImage(
                                model = user.profileImage,
                                contentDescription = "Profile Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp).clickable { onEditProfileClick() },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    if (user.verificationStatus == Constants.STATUS_VERIFIED) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, "Verified", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                    }
                }

                Text(
                    text = "${user.role.displayName} • ${user.department}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                if (user.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // 2. Metadata Info
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(label = "College", value = user.collegeName)
                    InfoRow(label = "Enrollment", value = user.enrollmentNumber)
                    InfoRow(label = "College ID", value = user.collegeId)
                    InfoRow(label = "Year", value = user.academicYear)
                    user.section?.let { if (it.isNotBlank()) InfoRow(label = "Section", value = it) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Quick Actions
            SectionHeader(title = "My Activity", actionText = null)
            SettingsRow(icon = Icons.Outlined.Description, title = "My Notes", onClick = { onMyActivityClick("Notes") })
            SettingsRow(icon = Icons.Outlined.Event, title = "My Events", onClick = { onMyActivityClick("Events") })
            SettingsRow(icon = Icons.Outlined.WorkOutline, title = "My Placements", onClick = { onMyActivityClick("Placements") })
            SettingsRow(icon = Icons.Outlined.Search, title = "My Lost & Found Posts", onClick = { onMyActivityClick("Lost & Found") })

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Account Settings
            SectionHeader(title = "Account Settings", actionText = null)
            SettingsRow(icon = Icons.Outlined.PersonOutline, title = "Edit Profile", onClick = onEditProfileClick)
            SettingsRow(icon = Icons.Outlined.Notifications, title = "Notifications", onClick = onNotificationSettingsClick)
            SettingsRow(icon = Icons.Outlined.Settings, title = "Settings", onClick = onSettingsClick)

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Support & About
            SectionHeader(title = "More", actionText = null)
            SettingsRow(icon = Icons.Outlined.PrivacyTip, title = "Privacy Policy", onClick = onPrivacyPolicyClick)
            SettingsRow(icon = Icons.Outlined.HelpOutline, title = "Help & Support", onClick = onHelpSupportClick)
            SettingsRow(icon = Icons.Outlined.Info, title = "About CampusConnect", onClick = onAboutClick)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
