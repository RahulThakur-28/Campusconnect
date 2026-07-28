package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.presentation.profile.components.*
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
import com.rahul.campusconnect.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
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
            CircularProgressIndicator(strokeWidth = 3.dp)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profile", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogoutClick()
                    }) {
                        Icon(Icons.Rounded.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // 1. Profile Header
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 8.dp,
                                shadowElevation = 8.dp
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
                                        Icon(
                                            Icons.Rounded.Person,
                                            null,
                                            modifier = Modifier.size(70.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                tonalElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.CameraAlt, "Edit", modifier = Modifier.size(18.dp), tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        RoleBadge(role = user.role)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.AccountBalance, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(6.dp))
                            Text(user.department, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(Icons.Rounded.School, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(6.dp))
                            Text(user.academicYear, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Activity Dashboard
                ProfileDashboardCard(
                    items = listOf(
                        DashboardItem("Notes", uiState.notesCount.toString(), Icons.Rounded.Description) { onMyActivityClick("Notes") },
                        DashboardItem("Events", uiState.eventsCount.toString(), Icons.Rounded.Event) { onMyActivityClick("Events") },
                        DashboardItem("Placement", uiState.placementsCount.toString(), Icons.Rounded.WorkOutline) { onMyActivityClick("Placements") },
                        DashboardItem("Found", uiState.lostFoundItemsCount.toString(), Icons.Rounded.Search) { onMyActivityClick("Lost & Found") },
                        DashboardItem("Notice", uiState.announcementsCount.toString(), Icons.Rounded.Campaign) { onMyActivityClick("Announcements") },
                        DashboardItem("Q&A", uiState.discussionsCount.toString(), Icons.Rounded.QuestionAnswer) { onMyActivityClick("Discussions") }
                    )
                )

                // 3. Admin Panel
                if (user.role == UserRole.ADMIN || user.role == UserRole.SUPER_ADMIN) {
                    ProfileSectionCard(title = "Admin Control Panel") {
                        SettingsRow(
                            icon = Icons.Rounded.VerifiedUser,
                            title = "Verification Requests",
                            subtitle = "Review pending user verifications",
                            onClick = onAdminVerificationClick
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        SettingsRow(
                            icon = Icons.Rounded.Group,
                            title = "User Management",
                            subtitle = "Manage roles and access permissions",
                            onClick = onUserManagementClick
                        )
                    }
                }

                // 4. Verification
                ProfileSectionCard(title = "Verification") {
                    val vStatus = uiState.verificationRequest?.status ?: ""
                    SettingsRow(
                        icon = if (user.isVerified) Icons.Rounded.Verified else Icons.Rounded.NewReleases,
                        title = when {
                            user.isVerified -> "Verified Account"
                            vStatus == "PENDING" -> "Verification Pending"
                            vStatus == "REJECTED" -> "Verification Rejected"
                            else -> "Get Verified"
                        },
                        subtitle = when {
                            user.isVerified -> "Full access to campus features"
                            vStatus == "PENDING" -> "Request is under review"
                            vStatus == "REJECTED" -> "Reason: ${uiState.verificationRequest?.rejectionReason}"
                            else -> "Apply to unlock premium features"
                        },
                        onClick = { if (!user.isVerified && vStatus != "PENDING") onVerificationClick() },
                        showChevron = !user.isVerified && vStatus != "PENDING"
                    )
                }

                // 5. Account Settings
                ProfileSectionCard(title = "Account Settings") {
                    SettingsRow(icon = Icons.Rounded.PersonOutline, title = "Edit Profile", onClick = onEditProfileClick)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsRow(icon = Icons.Rounded.Notifications, title = "Notifications", onClick = onNotificationSettingsClick)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsRow(icon = Icons.Rounded.Settings, title = "Settings", onClick = onSettingsClick)
                }

                // 6. Support & About
                ProfileSectionCard(title = "Support & About") {
                    SettingsRow(icon = Icons.Rounded.HelpOutline, title = "Help & Support", onClick = onHelpSupportClick)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsRow(icon = Icons.Rounded.PrivacyTip, title = "Privacy Policy", onClick = onPrivacyPolicyClick)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsRow(icon = Icons.Rounded.Info, title = "About CampusConnect", onClick = onAboutClick)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
