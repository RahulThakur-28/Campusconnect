package com.rahul.campusconnect.presentation.more.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.more.viewmodel.MoreViewModel
import com.rahul.campusconnect.presentation.notification.navigation.navigateToNotifications


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController,
    viewModel: MoreViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // ---------------------------------------------------------
            // PROFILE CARD
            // ---------------------------------------------------------

            ProfileCard(
                userName = uiState.userName,
                role = uiState.role,
                department = uiState.department,
                academicYear = uiState.academicYear,
                isVerified = uiState.isVerified
            )

            Spacer(modifier = Modifier.height(24.dp))


            // ---------------------------------------------------------
            // ACCOUNT
            // ---------------------------------------------------------

            MoreSectionTitle(
                title = "Account"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MoreMenuCard {

                MoreMenuItem(
                    title = "View Profile",
                    subtitle = "Manage your public profile",
                    icon = Icons.Outlined.Person,
                    iconContainerColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.primary,
                    onClick = {
                        navController.navigate(
                            AppRoutes.Profile.route
                        )
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "Edit Profile",
                    subtitle = "Update your profile information",
                    icon = Icons.Outlined.Edit,
                    iconContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.secondary,
                    onClick = {
                        navController.navigate(
                            AppRoutes.EditProfile.route
                        )
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "Verification Status",
                    subtitle = "Check your account verification",
                    icon = Icons.Outlined.VerifiedUser,
                    iconContainerColor =
                        MaterialTheme.colorScheme.tertiaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        // TODO: Verification screen
                    }
                )
            }


            Spacer(modifier = Modifier.height(24.dp))


            // ---------------------------------------------------------
            // CAMPUS SERVICES
            // ---------------------------------------------------------

            MoreSectionTitle(
                title = "Campus Services"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MoreMenuCard {

                MoreMenuItem(
                    title = "Notes",
                    subtitle = "Browse and upload study materials",
                    icon = Icons.Outlined.Description,
                    iconContainerColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.primary,
                    onClick = {
                       navController.navigate(AppRoutes.Notes.route)
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "Lost & Found",
                    subtitle = "View and report lost items",
                    icon = Icons.Outlined.Search,
                    iconContainerColor =
                        MaterialTheme.colorScheme.tertiaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        navController.navigate(
                            AppRoutes.LostFound.route
                        )
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "Notifications",
                    subtitle = "Stay updated with campus announcements",
                    icon = Icons.Outlined.Notifications,
                    iconContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.secondary,
                    onClick = {
                        navController.navigateToNotifications()

                    }
                )
            }


            Spacer(modifier = Modifier.height(24.dp))


            // ---------------------------------------------------------
            // PREFERENCES
            // ---------------------------------------------------------

            MoreSectionTitle(
                title = "Preferences"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MoreMenuCard {

                MoreMenuItem(
                    title = "Settings",
                    subtitle = "App settings and preferences",
                    icon = Icons.Outlined.Settings,
                    iconContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    iconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        navController.navigate(
                            AppRoutes.Settings.route
                        )
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "Help & Support",
                    subtitle = "Get help with CampusConnect",
                    icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    iconContainerColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    iconColor =
                        MaterialTheme.colorScheme.primary,
                    onClick = {
                        navController.navigate(
                            AppRoutes.HelpSupport.route
                        )
                    }
                )

                MoreDivider()

                MoreMenuItem(
                    title = "About CampusConnect",
                    subtitle = "App information and policies",
                    icon = Icons.Outlined.Info,
                    iconContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    iconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        navController.navigate(
                            AppRoutes.About.route
                        )
                    }
                )
            }


            Spacer(modifier = Modifier.height(20.dp))


            // ---------------------------------------------------------
            // LOGOUT
            // ---------------------------------------------------------

            LogoutButton(
                onClick = {
                    viewModel.logout()
                }
            )


            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


// =================================================================
// PROFILE CARD
// =================================================================

@Composable
private fun ProfileCard(
    userName: String,
    role: String,
    department: String,
    academicYear: String,
    isVerified: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Profile Avatar
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.width(16.dp)
                )


                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )


                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )


                    if (isVerified) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )

                            Spacer(
                                modifier = Modifier.width(5.dp)
                            )

                            Text(
                                text = role,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    } else {

                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
                    .copy(alpha = 0.5f)
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // Profile Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                ProfileInfoItem(
                    value = department,
                    label = "Department",
                    modifier = Modifier.weight(1f)
                )

                ProfileInfoDivider()

                ProfileInfoItem(
                    value = academicYear,
                    label = "Year",
                    modifier = Modifier.weight(1f)
                )

                ProfileInfoDivider()

                ProfileInfoItem(
                    value = if (isVerified) "Verified" else "Student",
                    label = "Status",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


// =================================================================
// PROFILE INFO
// =================================================================

@Composable
private fun ProfileInfoItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun ProfileInfoDivider() {

    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(
                MaterialTheme.colorScheme.outlineVariant
            )
    )
}


// =================================================================
// SECTION TITLE
// =================================================================

@Composable
private fun MoreSectionTitle(
    title: String
) {

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(
            horizontal = 4.dp
        )
    )
}


// =================================================================
// MENU CARD
// =================================================================

@Composable
private fun MoreMenuCard(
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            content = content
        )
    }
}


// =================================================================
// MENU ITEM
// =================================================================

@Composable
private fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon Container
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = iconContainerColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(21.dp),
                tint = iconColor
            )
        }


        Spacer(
            modifier = Modifier.width(14.dp)
        )


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Spacer(
            modifier = Modifier.width(8.dp)
        )


        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
                .copy(alpha = 0.6f)
        )
    }
}


// =================================================================
// DIVIDER
// =================================================================

@Composable
private fun MoreDivider() {

    HorizontalDivider(
        modifier = Modifier.padding(
            start = 72.dp,
            end = 16.dp
        ),
        color = MaterialTheme.colorScheme.outlineVariant
            .copy(alpha = 0.45f)
    )
}


// =================================================================
// LOGOUT
// =================================================================

@Composable
private fun LogoutButton(
    onClick: () -> Unit
) {

    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = "Log out",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}