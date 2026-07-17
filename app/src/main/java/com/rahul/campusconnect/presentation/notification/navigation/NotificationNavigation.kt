package com.rahul.campusconnect.presentation.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.model.NotificationType
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.navigateToAnnouncementDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notification.screen.NotificationScreen
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails

fun NavController.navigateToNotifications(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Notifications.route, navOptions)
}

fun NavGraphBuilder.notificationGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.Notifications.route) {
        NotificationScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onNotificationClick = { notification ->
                when (notification.type) {
                    NotificationType.ANNOUNCEMENT -> {
                        notification.referenceId?.let {
                            navController.navigateToAnnouncementDetails(it)
                        }
                    }
                    NotificationType.EVENT -> {
                        notification.referenceId?.let {
                            navController.navigateToEventDetails(it)
                        }
                    }
                    NotificationType.PLACEMENT -> {
                        notification.referenceId?.let {
                            navController.navigateToPlacementDetails(it)
                        }
                    }
                    NotificationType.LOST_FOUND -> {
                        notification.referenceId?.let {
                            navController.navigateToLostFoundDetails(it)
                        }
                    }
                    NotificationType.NOTE -> {
                        notification.referenceId?.let {
                            navController.navigateToNoteDetails(it)
                        }
                    }
                    NotificationType.GENERAL -> {
                        // Stay on screen or navigate to general details
                    }
                }
            }
        )
    }
}
