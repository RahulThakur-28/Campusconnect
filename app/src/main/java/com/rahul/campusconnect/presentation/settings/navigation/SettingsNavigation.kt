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

fun NavController.navigateToChangePassword() {
    this.navigate(AppRoutes.ChangePassword.route)
}

fun NavController.navigateToDeleteAccount() {
    this.navigate(AppRoutes.DeleteAccount.route)
}

fun NavController.navigateToBugReport() {
    this.navigate(AppRoutes.BugReport.route)
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    onEditProfileClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    composable(route = AppRoutes.Settings.route) {
        SettingsScreen(
            onBackClick = { navController.popBackStack() },
            onEditProfileClick = onEditProfileClick,
            onChangePasswordClick = { navController.navigateToChangePassword() },
            onNotificationSettingsClick = { navController.navigateToNotificationSettings() },
            onAboutClick = { navController.navigateToAbout() },
            onHelpSupportClick = { navController.navigateToHelpSupport() },
            onPrivacyPolicyClick = { navController.navigateToPrivacyPolicy() },
            onTermsClick = { navController.navigateToTerms() },
            onBugReportClick = { navController.navigateToBugReport() },
            onDeleteAccountClick = { navController.navigateToDeleteAccount() },
            onLogoutSuccess = onLogoutSuccess
        )
    }

    composable(route = AppRoutes.ChangePassword.route) {
        ChangePasswordScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(route = AppRoutes.DeleteAccount.route) {
        DeleteAccountScreen(
            onBackClick = { navController.popBackStack() },
            onDeleteSuccess = onLogoutSuccess
        )
    }

    composable(route = AppRoutes.BugReport.route) {
        BugReportScreen(
            onBackClick = { navController.popBackStack() }
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
            onBackClick = { navController.popBackStack() },
            onBugReportClick = { navController.navigateToBugReport() }
        )
    }
}
