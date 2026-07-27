package com.rahul.campusconnect.presentation.bottomnavigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.announcement.navigation.announcementGraph
import com.rahul.campusconnect.presentation.discussion.navigation.discussionGraph
import com.rahul.campusconnect.presentation.discussion.navigation.navigateToDiscussion
import com.rahul.campusconnect.presentation.event.navigation.eventGraph
import com.rahul.campusconnect.presentation.event.navigation.navigateToCreateEvent
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.home.HomeScreen
import com.rahul.campusconnect.presentation.lostfound.navigation.lostFoundGraph
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.more.navigation.moreGraph
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notes.navigation.notesGraph
import com.rahul.campusconnect.presentation.notification.navigation.notificationGraph
import com.rahul.campusconnect.presentation.placement.navigation.navigateToCreatePlacement
import com.rahul.campusconnect.presentation.placement.navigation.navigateToEditPlacement
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.placement.navigation.placementGraph
import com.rahul.campusconnect.presentation.profile.navigation.profileGraph
import com.rahul.campusconnect.presentation.search.navigation.searchGraph
import com.rahul.campusconnect.presentation.settings.navigation.settingsGraph

@Composable
fun MainNavigation(
    navController: NavHostController,
    rootNavController: NavController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home.route,
        modifier = modifier
    ) {
        composable(route = AppRoutes.Home.route) {
            HomeScreen(navController = navController)
        }

        eventGraph(
            navController = navController,
            onBackClick = { navController.popBackStack() },
            onViewDiscussionClick = { eventId ->
                navController.navigateToDiscussion(eventId, DiscussionParentType.EVENT)
            }
        )

        placementGraph(
            navController = navController,
            onBackClick = { navController.popBackStack() },
            onPlacementClick = { id -> navController.navigateToPlacementDetails(id) },
            onEditPlacementClick = { id -> navController.navigateToEditPlacement(id) },
            onViewDiscussionClick = { placementId ->
                navController.navigateToDiscussion(placementId, DiscussionParentType.PLACEMENT)
            },
            onCreatePlacementClick = { navController.navigateToCreatePlacement() }
        )

        announcementGraph(navController = navController)
        moreGraph(navController = navController)
        notesGraph(navController)
        lostFoundGraph(navController)
        discussionGraph(navController)

        profileGraph(
            navController = navController,
            onLogoutClick = {
                rootNavController.navigate(AppRoutes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSettingsClick = { navController.navigate(AppRoutes.Settings.route) },
            onNoteClick = { noteId -> navController.navigateToNoteDetails(noteId) },
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) },
            onPlacementClick = { placementId -> navController.navigateToPlacementDetails(placementId) },
            onLostFoundClick = { itemId -> navController.navigateToLostFoundDetails(itemId) },
            onBackClick = { navController.popBackStack() }
        )

        settingsGraph(
            navController = navController,
            onEditProfileClick = { navController.navigate(AppRoutes.EditProfile.route) },
            onLogoutSuccess = {
                rootNavController.navigate(AppRoutes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )

        notificationGraph(navController)
        searchGraph(navController)
    }
}
