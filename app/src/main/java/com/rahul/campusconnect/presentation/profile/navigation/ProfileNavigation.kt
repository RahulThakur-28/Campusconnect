package com.rahul.campusconnect.presentation.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.profile.screen.*
import com.rahul.campusconnect.presentation.settings.navigation.navigateToAbout
import com.rahul.campusconnect.presentation.settings.navigation.navigateToHelpSupport
import com.rahul.campusconnect.presentation.settings.navigation.navigateToNotificationSettings
import com.rahul.campusconnect.presentation.settings.navigation.navigateToPrivacyPolicy

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Profile.route, navOptions)
}

fun NavController.navigateToEditProfile() {
    this.navigate(AppRoutes.EditProfile.route)
}

fun NavController.navigateToMyActivity(category: String) {
    this.navigate("my_activity/$category")
}

fun NavController.navigateToRequestVerification() {
    this.navigate(AppRoutes.RequestVerification.route)
}

fun NavController.navigateToAdminVerification() {
    this.navigate(AppRoutes.AdminVerificationRequests.route)
}

fun NavController.navigateToUserManagement() {
    this.navigate("user_management")
}

fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onPlacementClick: (String) -> Unit,
    onLostFoundClick: (String) -> Unit
) {
    composable(route = AppRoutes.Profile.route) {
        ProfileScreen(
            onEditProfileClick = { navController.navigateToEditProfile() },
            onMyActivityClick = { category ->
                navController.navigateToMyActivity(category)
            },
            onSettingsClick = onSettingsClick,
            onAboutClick = { navController.navigateToAbout() },
            onPrivacyPolicyClick = { navController.navigateToPrivacyPolicy() },
            onHelpSupportClick = { navController.navigateToHelpSupport() },
            onLogoutClick = onLogoutClick,
            onNotificationSettingsClick = { navController.navigateToNotificationSettings() },
            onVerificationClick = { navController.navigateToRequestVerification() },
            onAdminVerificationClick = { navController.navigateToAdminVerification() },
            onUserManagementClick = { navController.navigateToUserManagement() }
        )
    }

    composable(route = AppRoutes.EditProfile.route) {
        EditProfileScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.RequestVerification.route) {
        RequestVerificationScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.AdminVerificationRequests.route) {
        AdminVerificationScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
    
    composable(route = "user_management") {
        UserManagementScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = AppRoutes.MyActivity.route,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: "Notes"
        MyActivityScreen(
            initialTab = category,
            onBackClick = { navController.popBackStack() },
            onNoteClick = onNoteClick,
            onEventClick = onEventClick,
            onPlacementClick = onPlacementClick,
            onLostFoundClick = onLostFoundClick,
            onAnnouncementClick = { id -> navController.navigate("announcement_details/$id") },
            onDiscussionClick = { id -> navController.navigate("discussion_details/$id") }
        )
    }
}
