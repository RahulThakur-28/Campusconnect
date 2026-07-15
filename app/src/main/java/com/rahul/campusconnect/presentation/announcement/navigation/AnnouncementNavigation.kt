package com.rahul.campusconnect.presentation.announcement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.screen.AnnouncementDetailsScreen
import com.rahul.campusconnect.presentation.announcement.screen.AnnouncementsScreen
import com.rahul.campusconnect.presentation.announcement.screen.CreateAnnouncementScreen

fun NavController.navigateToAnnouncements(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Announcements.route, navOptions)
}

fun NavController.navigateToAnnouncementDetails(announcementId: String) {
    this.navigate("announcement_details/$announcementId")
}

fun NavController.navigateToCreateAnnouncement() {
    this.navigate(AppRoutes.CreateAnnouncement.route)
}

fun NavGraphBuilder.announcementGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.Announcements.route) {
        AnnouncementsScreen(
            onAnnouncementClick = { announcementId ->
                navController.navigateToAnnouncementDetails(announcementId)
            },
            onCreateAnnouncementClick = {
                navController.navigateToCreateAnnouncement()
            }
        )
    }

    composable(
        route = AppRoutes.AnnouncementDetails.route,
        arguments = listOf(navArgument("announcementId") { type = NavType.StringType })
    ) { backStackEntry ->
        val announcementId = backStackEntry.arguments?.getString("announcementId") ?: return@composable
        AnnouncementDetailsScreen(
            announcementId = announcementId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable(route = AppRoutes.CreateAnnouncement.route) {
        CreateAnnouncementScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onPublishSuccess = {
                navController.popBackStack()
            }
        )
    }
}
