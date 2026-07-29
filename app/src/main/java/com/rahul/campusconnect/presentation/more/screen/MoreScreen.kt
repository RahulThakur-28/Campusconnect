package com.rahul.campusconnect.presentation.more.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.more.viewmodel.MoreViewModel
import com.rahul.campusconnect.presentation.notification.navigation.navigateToNotifications
import com.rahul.campusconnect.presentation.profile.components.ProfileSectionCard
import com.rahul.campusconnect.presentation.profile.components.RoleBadge
import com.rahul.campusconnect.presentation.profile.components.SettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(AppRoutes.Login.route) {
                launchSingleTop = true
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { navController.navigateToNotifications() }) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(
                        onClick = {
                            showLogoutDialog = true
                        },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "Logout"
                            )
                        }
                    }
                }

            )


        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. PROFILE HEADER SECTION
            Box(modifier = Modifier.fillMaxWidth()) {
                // Gradient Background
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
                        .padding(top = 60.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Elevated Profile Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Surface(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 8.dp,
                                shadowElevation = 8.dp
                            ) {
                                if (uiState.profilePictureUrl?.isNotBlank() == true) {
                                    AsyncImage(
                                        model = uiState.profilePictureUrl,
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(60.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = uiState.userName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            RoleBadge(role = uiState.role)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            // Metadata Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MetadataItem(icon = Icons.Rounded.AccountBalance, value = uiState.department, label = "Department", modifier = Modifier.weight(1f))
                                VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                MetadataItem(icon = Icons.Rounded.School, value = uiState.academicYear, label = "Year", modifier = Modifier.weight(1f))
                                VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                MetadataItem(icon = Icons.Rounded.Domain, value = uiState.collegeName, label = "College", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. ACCOUNT SECTION
            ProfileSectionCard(title = "Account") {
                SettingsRow(
                    icon = Icons.Rounded.AccountCircle,
                    title = "View Profile",
                    subtitle = "Manage your public profile and bio",
                    onClick = { navController.navigate(AppRoutes.Profile.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.ManageAccounts,
                    title = "Edit Profile",
                    subtitle = "Update personal and academic details",
                    onClick = { navController.navigate(AppRoutes.EditProfile.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.VerifiedUser,
                    title = "Verification Status",
                    subtitle = "Manage account verification",
                    onClick = { navController.navigate(AppRoutes.RequestVerification.route) }
                )
            }

            // 3. CAMPUS SERVICES SECTION
            ProfileSectionCard(title = "Campus Services") {
                SettingsRow(
                    icon = Icons.Rounded.Description,
                    title = "Study Notes",
                    subtitle = "Access shared materials and resources",
                    onClick = { navController.navigate(AppRoutes.Notes.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.Search,
                    title = "Lost & Found",
                    subtitle = "Recover or report items on campus",
                    onClick = { navController.navigate(AppRoutes.LostFound.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.NotificationsActive,
                    title = "Notifications",
                    subtitle = "Stay updated with recent alerts",
                    onClick = { navController.navigateToNotifications() }
                )
            }

            // 4. PREFERENCES SECTION
            ProfileSectionCard(title = "Preferences") {
                SettingsRow(
                    icon = Icons.Rounded.Settings,
                    title = "Settings",
                    subtitle = "App settings and dark mode",
                    onClick = { navController.navigate(AppRoutes.Settings.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    title = "Help & Support",
                    subtitle = "FAQs and direct contact",
                    onClick = { navController.navigate(AppRoutes.HelpSupport.route) }
                )
            }

            // 5. SUPPORT & ABOUT SECTION
            ProfileSectionCard(title = "Support & About") {
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    title = "About CampusConnect",
                    subtitle = "Showcase and application info",
                    onClick = { navController.navigate(AppRoutes.About.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = { navController.navigate(AppRoutes.PrivacyPolicy.route) }
                )
                ItemDivider()
                SettingsRow(
                    icon = Icons.Rounded.Gavel,
                    title = "Terms & Conditions",
                    onClick = { navController.navigate(AppRoutes.TermsConditions.route) }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isLoading) showLogoutDialog = false
            },
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                    },
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Logout")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    },
                    enabled = !uiState.isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetadataItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}


@Composable
private fun ItemDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}
