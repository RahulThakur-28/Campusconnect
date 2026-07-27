package com.rahul.campusconnect.presentation.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.navigateToAnnouncementDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notification.screen.NotificationScreen
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.profile.navigation.navigateToProfile

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
                        notification.relatedId?.let {
                            navController.navigateToAnnouncementDetails(it)
                        }
                    }
                    NotificationType.EVENT -> {
                        notification.relatedId?.let {
                            navController.navigateToEventDetails(it)
                        }
                    }
                    NotificationType.PLACEMENT -> {
                        notification.relatedId?.let {
                            navController.navigateToPlacementDetails(it)
                        }
                    }
                    NotificationType.LOST_FOUND -> {
                        notification.relatedId?.let {
                            navController.navigateToLostFoundDetails(it)
                        }
                    }
                    NotificationType.VERIFICATION_APPROVED,
                    NotificationType.VERIFICATION_REJECTED -> {
                        navController.navigateToProfile()
                    }
                    NotificationType.DISCUSSION_REPLY -> {
                        notification.relatedId?.let {
                            navController.navigate("discussion_details/$it")
                        }
                    }
                    NotificationType.GENERAL -> { }
                }
            }
        )
    }
}
