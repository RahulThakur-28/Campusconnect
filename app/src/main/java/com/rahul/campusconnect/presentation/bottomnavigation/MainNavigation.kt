package com.rahul.campusconnect.presentation.bottomnavigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.announcementGraph
import com.rahul.campusconnect.presentation.event.navigation.eventGraph
import com.rahul.campusconnect.presentation.event.navigation.navigateToCreateEvent
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToPastEvents
import com.rahul.campusconnect.presentation.event.navigation.navigateToUpcomingEvents
import com.rahul.campusconnect.presentation.home.HomeScreen
import com.rahul.campusconnect.presentation.lostfound.navigation.lostFoundGraph
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.more.navigation.moreGraph
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notes.navigation.notesGraph
import com.rahul.campusconnect.presentation.notification.navigation.notificationGraph
import com.rahul.campusconnect.presentation.placement.navigation.navigateToCreatePlacement
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.placement.navigation.placementGraph
import com.rahul.campusconnect.presentation.profile.navigation.profileGraph
import com.rahul.campusconnect.presentation.search.navigation.searchGraph
import com.rahul.campusconnect.presentation.settings.navigation.settingsGraph

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home.route,
        modifier = modifier
    ) {

        // ---------------- Home ----------------

        composable(
            route = AppRoutes.Home.route
        ) {
            HomeScreen(
                navController = navController
            )
        }


        // ---------------- Events ----------------

        eventGraph(
            onBackClick = {
                navController.popBackStack()
            },

            onEventClick = { eventId ->
                navController.navigateToEventDetails(eventId)
            },

            onViewDiscussionClick = {
                // TODO: Discussion navigation
            },

            onCreateEventClick = {
                navController.navigateToCreateEvent()
            },

            onEventCreated = {
                navController.popBackStack()
            }
            ,
            onUpcomingEventsClick = {
                navController.navigateToUpcomingEvents()
            },

            onPastEventsClick = {
                navController.navigateToPastEvents()
            }

        )


        // ---------------- Placements ----------------

        placementGraph(

            onBackClick = {
                navController.popBackStack()
            },

            onPlacementClick = { placementId ->
                navController.navigateToPlacementDetails(placementId)
            },

            onViewDiscussionClick = {
                // TODO: Navigate to Placement Discussion
            },

            onCreatePlacementClick = {
                navController.navigateToCreatePlacement()
            },

            onPlacementUpdated = {
                navController.popBackStack()
            },

            onPlacementCreated = {
                navController.popBackStack()
            }
        )


        // ---------------- Announcements ----------------

        announcementGraph(
            navController = navController
        )




        // ---------------- More ----------------

        moreGraph(
            navController = navController
        )





        // ---------------- Notes ----------------
        notesGraph(navController)





        // ---------------- Lost & Found ----------------
        lostFoundGraph(navController)






        // ---------------- Profile ----------------
        profileGraph(
            navController = navController,
            onLogoutClick = { /* TODO */ },
            onSettingsClick = { navController.navigate(AppRoutes.Settings.route) },
            onNoteClick = { noteId -> navController.navigateToNoteDetails(noteId) },
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) },
            onPlacementClick = { placementId -> navController.navigateToPlacementDetails(placementId) },
            onLostFoundClick = { itemId -> navController.navigateToLostFoundDetails(itemId) }
        )



        // ---------------- Settings ----------------
        settingsGraph(
            navController = navController,
            onEditProfileClick = { navController.navigate(AppRoutes.EditProfile.route) }
        )

        // ---------------- Notifications ----------------
        notificationGraph(navController)

        // ---------------- Search ----------------
        searchGraph(navController)

    }






}