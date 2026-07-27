package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.rahul.campusconnect.domain.model.UserRole
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
    onVerificationClick: () -> Unit,
    onAdminVerificationClick: () -> Unit,
    onUserManagementClick: () -> Unit,
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // 1. Profile Header
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

                    Text(text = user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = user.role.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (user.isVerified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Verified, "Verified", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                            Text(
                                text = "Verified",
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${user.department} • ${user.academicYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 2. Statistics Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard("Notes", uiState.notesCount.toString(), Modifier.weight(1f))
                        StatCard("Events", uiState.eventsCount.toString(), Modifier.weight(1f))
                        StatCard("Placement", uiState.placementsCount.toString(), Modifier.weight(1f))
                        StatCard("Announce", uiState.announcementsCount.toString(), Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Admin Panel
                if (user.role == UserRole.ADMIN) {
                    SectionHeader(title = "Admin Panel", actionText = null)
                    SettingsRow(
                        icon = Icons.Outlined.AdminPanelSettings,
                        title = "Verification Requests",
                        subtitle = "Review student & faculty requests",
                        onClick = onAdminVerificationClick
                    )
                    SettingsRow(
                        icon = Icons.Outlined.Group,
                        title = "User Management",
                        subtitle = "Manage roles and promote users",
                        onClick = onUserManagementClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. My Content Section
                SectionHeader(title = "My Activity", actionText = null)
                
                SettingsRow(icon = Icons.Outlined.Description, title = "My Notes", onClick = { onMyActivityClick("Notes") })
                SettingsRow(icon = Icons.Outlined.QuestionAnswer, title = "My Discussions", onClick = { onMyActivityClick("Discussions") })
                SettingsRow(icon = Icons.Outlined.Search, title = "My Lost & Found", onClick = { onMyActivityClick("Lost & Found") })

                if (user.role == UserRole.VERIFIED_TEACHER || user.role == UserRole.ADMIN) {
                    SettingsRow(icon = Icons.Outlined.Event, title = "My Events", onClick = { onMyActivityClick("Events") })
                    SettingsRow(icon = Icons.Outlined.Campaign, title = "My Announcements", onClick = { onMyActivityClick("Announcements") })
                }

                if (user.role == UserRole.PLACEMENT_CELL || user.role == UserRole.ADMIN) {
                    SettingsRow(icon = Icons.Outlined.WorkOutline, title = "My Placements", onClick = { onMyActivityClick("Placements") })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Verification Section
                if (!user.isVerified) {
                    SectionHeader(title = "Verification", actionText = null)
                    
                    val status = uiState.verificationRequest?.status ?: ""
                    
                    SettingsRow(
                        icon = Icons.Outlined.VerifiedUser,
                        title = when(status) {
                            "PENDING" -> "Verification Pending"
                            "REJECTED" -> "Verification Rejected"
                            else -> "Request Verification"
                        },
                        subtitle = when(status) {
                            "PENDING" -> "Your request is being reviewed"
                            "REJECTED" -> "Reason: ${uiState.verificationRequest?.rejectionReason}\nTap to retry"
                            else -> "Get verified to unlock features"
                        },
                        onClick = { if (status != "PENDING") onVerificationClick() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    SectionHeader(title = "Verification", actionText = null)
                    SettingsRow(
                        icon = Icons.Outlined.Verified,
                        title = "Verified Account ✅",
                        subtitle = "You have full access to campus features",
                        onClick = { },
                        showChevron = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 6. Account Settings
                SectionHeader(title = "Account Settings", actionText = null)
                SettingsRow(icon = Icons.Outlined.PersonOutline, title = "Edit Profile", onClick = onEditProfileClick)
                SettingsRow(icon = Icons.Outlined.Notifications, title = "Notifications", onClick = onNotificationSettingsClick)
                SettingsRow(icon = Icons.Outlined.Settings, title = "Settings", onClick = onSettingsClick)

                Spacer(modifier = Modifier.height(16.dp))

                // 7. More
                SectionHeader(title = "More", actionText = null)
                SettingsRow(icon = Icons.Outlined.PrivacyTip, title = "Privacy Policy", onClick = onPrivacyPolicyClick)
                SettingsRow(icon = Icons.Outlined.HelpOutline, title = "Help & Support", onClick = onHelpSupportClick)
                SettingsRow(icon = Icons.Outlined.Info, title = "About CampusConnect", onClick = onAboutClick)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
