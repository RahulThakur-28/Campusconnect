package com.rahul.campusconnect.presentation.event.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.event.screen.*
import com.rahul.campusconnect.presentation.event.viewmodel.EventListType

const val EVENTS_ROUTE = "events"
const val EVENT_DETAILS_ROUTE = "event_details/{eventId}"

fun NavController.navigateToEvents(navOptions: NavOptions? = null) {
    navigate(EVENTS_ROUTE, navOptions)
}

fun NavController.navigateToEventDetails(eventId: String) {
    navigate("event_details/$eventId")
}

fun NavController.navigateToCreateEvent() {
    navigate(AppRoutes.CreateEvent.route)
}

fun NavController.navigateToEditEvent(eventId: String) {
    navigate("edit_event/$eventId")
}

fun NavController.navigateToUpcomingEvents() {
    navigate(AppRoutes.UpcomingEvents.route)
}

fun NavController.navigateToPastEvents() {
    navigate(AppRoutes.PastEvents.route)
}

fun NavController.navigateToMyEvents() {
    navigate(AppRoutes.MyEvents.route)
}

fun NavGraphBuilder.eventGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    onViewDiscussionClick: (String) -> Unit
) {
    composable(route = EVENTS_ROUTE) {
        EventsScreen(
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) },
            onCreateEventClick = { navController.navigateToCreateEvent() },
            navController = navController
        )
    }

    composable(route = EVENT_DETAILS_ROUTE) { backStackEntry ->
        val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
        EventDetailsScreen(
            eventId = eventId,
            onBackClick = onBackClick,
            onEditClick = { id -> navController.navigateToEditEvent(id) },
            onViewDiscussionClick = { onViewDiscussionClick(eventId) },
            navController = navController
        )
    }

    composable(route = AppRoutes.CreateEvent.route) {
        CreateEventScreen(
            onBackClick = onBackClick,
            navController = navController
        )
    }

    composable(route = AppRoutes.EditEvent.route) { backStackEntry ->
        val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
        EditEventScreen(
            eventId = eventId,
            onBackClick = onBackClick,
            navController = navController
        )
    }

    composable(route = AppRoutes.UpcomingEvents.route) {
        EventListScreen(
            type = EventListType.UPCOMING,
            onBackClick = onBackClick,
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) }
        )
    }

    composable(route = AppRoutes.PastEvents.route) {
        EventListScreen(
            type = EventListType.PAST,
            onBackClick = onBackClick,
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) }
        )
    }

    composable(route = AppRoutes.MyEvents.route) {
        MyEventsScreen(
            onBackClick = onBackClick,
            onEventClick = { eventId -> navController.navigateToEventDetails(eventId) },
            navController = navController
        )
    }
}
