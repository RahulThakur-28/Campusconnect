package com.rahul.campusconnect.presentation.event.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.event.screen.CreateEventScreen
import com.rahul.campusconnect.presentation.event.screen.EventDetailsScreen
import com.rahul.campusconnect.presentation.event.screen.EventListScreen
import com.rahul.campusconnect.presentation.event.screen.EventsScreen
import com.rahul.campusconnect.presentation.event.viewmodel.EventListType

const val EVENTS_ROUTE = "events"
const val EVENT_DETAILS_ROUTE = "event_details/{eventId}"


fun NavController.navigateToEvents(
    navOptions: NavOptions? = null
) {
    navigate(EVENTS_ROUTE, navOptions)
}


fun NavController.navigateToEventDetails(
    eventId: String
) {
    navigate("event_details/$eventId")
}


fun NavController.navigateToCreateEvent() {
    navigate(AppRoutes.CreateEvent.route)
}


fun NavController.navigateToUpcomingEvents() {
    navigate(AppRoutes.UpcomingEvents.route)
}


fun NavController.navigateToPastEvents() {
    navigate(AppRoutes.PastEvents.route)
}


fun NavGraphBuilder.eventGraph(

    onBackClick: () -> Unit,

    onViewDiscussionClick: (String) -> Unit,

    onCreateEventClick: () -> Unit,

    onEventClick: (String) -> Unit,

    onEventCreated: () -> Unit,

    onUpcomingEventsClick: () -> Unit,

    onPastEventsClick: () -> Unit
) {

    // ------------------------------------------------
    // Events Main Screen
    // ------------------------------------------------

    composable(
        route = EVENTS_ROUTE
    ) {

        EventsScreen(
            onEventClick = onEventClick,
            onCreateEventClick = onCreateEventClick,

            onUpcomingEventsClick = onUpcomingEventsClick,
            onPastEventsClick = onPastEventsClick
        )
    }


    // ------------------------------------------------
    // Event Details
    // ------------------------------------------------

    composable(
        route = EVENT_DETAILS_ROUTE
    ) { backStackEntry ->

        val eventId =
            backStackEntry.arguments?.getString("eventId")
                ?: return@composable

        EventDetailsScreen(
            eventId = eventId,

            onBackClick = onBackClick,

            onViewDiscussionClick = {
                onViewDiscussionClick(eventId)
            }
        )
    }


    // ------------------------------------------------
    // Create Event
    // ------------------------------------------------

    composable(
        route = AppRoutes.CreateEvent.route
    ) {

        CreateEventScreen(
            onBackClick = onBackClick,
            onEventCreated = onEventCreated
        )
    }


    // ------------------------------------------------
    // Upcoming Events
    // ------------------------------------------------

    composable(
        route = AppRoutes.UpcomingEvents.route
    ) {

        EventListScreen(
            type = EventListType.UPCOMING,

            onBackClick = onBackClick,

            onEventClick = onEventClick
        )
    }


    // ------------------------------------------------
    // Past Events
    // ------------------------------------------------

    composable(
        route = AppRoutes.PastEvents.route
    ) {

        EventListScreen(
            type = EventListType.PAST,

            onBackClick = onBackClick,

            onEventClick = onEventClick
        )
    }
}