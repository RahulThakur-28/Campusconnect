package com.rahul.campusconnect.presentation.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.settings.screen.*

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Settings.route, navOptions)
}

fun NavController.navigateToNotificationSettings() {
    this.navigate(AppRoutes.NotificationSettings.route)
}

fun NavController.navigateToAbout() {
    this.navigate(AppRoutes.About.route)
}

fun NavController.navigateToPrivacyPolicy() {
    this.navigate(AppRoutes.PrivacyPolicy.route)
}

fun NavController.navigateToTerms() {
    this.navigate(AppRoutes.TermsConditions.route)
}

fun NavController.navigateToHelpSupport() {
    this.navigate(AppRoutes.HelpSupport.route)
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    onEditProfileClick: () -> Unit
) {
    composable(route = AppRoutes.Settings.route) {
        SettingsScreen(
            onBackClick = { navController.popBackStack() },
            onEditProfileClick = onEditProfileClick,
            onNotificationSettingsClick = { navController.navigateToNotificationSettings() },
            onAboutClick = { navController.navigateToAbout() },
            onHelpSupportClick = { navController.navigateToHelpSupport() },
            onPrivacyPolicyClick = { navController.navigateToPrivacyPolicy() },
            onTermsClick = { navController.navigateToTerms() }
        )
    }

    composable(route = AppRoutes.NotificationSettings.route) {
        NotificationSettingsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.About.route) {
        AboutScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.PrivacyPolicy.route) {
        PrivacyPolicyScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.TermsConditions.route) {
        TermsConditionsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.HelpSupport.route) {
        HelpSupportScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
