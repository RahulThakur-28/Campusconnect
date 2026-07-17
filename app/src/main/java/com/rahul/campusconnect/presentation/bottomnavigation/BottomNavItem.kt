package com.rahul.campusconnect.presentation.bottomnavigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.rahul.campusconnect.navigation.AppRoutes

/**
 * Represents one item in the Bottom Navigation.
 */
data class BottomNavItem(

    /**
     * Navigation route.
     */
    val route: String,

    /**
     * Text shown below icon.
     */
    val title: String,

    /**
     * Material Icon.
     */
    val icon: ImageVector
)

val bottomNavigationItems = listOf(

    BottomNavItem(
        route = AppRoutes.Home.route,
        title = "Home",
        icon = Icons.Outlined.Home
    ),

    BottomNavItem(
        route = AppRoutes.Events.route,
        title = "Events",
        icon = Icons.Outlined.Event
    ),

    BottomNavItem(
        route = AppRoutes.Placements.route,
        title = "Placements",
        icon = Icons.Outlined.WorkOutline
    ),

    BottomNavItem(
        route = AppRoutes.Announcements.route,
        title = "Announcements",
        icon = Icons.Outlined.Campaign
    ),

    BottomNavItem(
        route = AppRoutes.More.route,
        title = "More",
        icon = Icons.Outlined.Menu
    )
)