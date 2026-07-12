package com.rahul.campusconnect.presentation.event.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.presentation.event.screen.EventDetailsScreen
import com.rahul.campusconnect.presentation.event.screen.EventsScreen

const val EVENTS_ROUTE = "events"
const val EVENT_DETAILS_ROUTE = "event_details/{eventId}"

fun NavController.navigateToEvents(navOptions: NavOptions? = null) {
    this.navigate(EVENTS_ROUTE, navOptions)
}

fun NavController.navigateToEventDetails(eventId: String) {
    this.navigate("event_details/$eventId")
}
fun NavGraphBuilder.eventGraph(
    onBackClick: () -> Unit,
    onViewDiscussionClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    onEventClick: (String) -> Unit
) {

    composable(route = EVENTS_ROUTE) {

        EventsScreen(
            onEventClick = onEventClick,
            onCreateEventClick = onCreateEventClick
        )
    }

    composable(route = EVENT_DETAILS_ROUTE) { backStackEntry ->

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
}