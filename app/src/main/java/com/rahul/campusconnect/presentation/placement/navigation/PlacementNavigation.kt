package com.rahul.campusconnect.presentation.placement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.presentation.placement.screen.CreatePlacementScreen
import com.rahul.campusconnect.presentation.placement.screen.EditPlacementScreen
import com.rahul.campusconnect.presentation.placement.screen.PlacementDetailsScreen
import com.rahul.campusconnect.presentation.placement.screen.PlacementsScreen

const val PLACEMENTS_ROUTE = "placements"
const val PLACEMENT_DETAILS_ROUTE = "placement_details/{placementId}"
const val CREATE_PLACEMENT_ROUTE = "create_placement"
const val EDIT_PLACEMENT_ROUTE = "edit_placement/{placementId}"

fun NavController.navigateToPlacements(navOptions: NavOptions? = null) {
    this.navigate(PLACEMENTS_ROUTE, navOptions)
}

fun NavController.navigateToPlacementDetails(placementId: String) {
    this.navigate("placement_details/$placementId")
}

fun NavController.navigateToCreatePlacement() {
    this.navigate(CREATE_PLACEMENT_ROUTE)
}

fun NavController.navigateToEditPlacement(placementId: String) {
    this.navigate("edit_placement/$placementId")
}

fun NavGraphBuilder.placementGraph(
    onBackClick: () -> Unit,
    onPlacementClick: (String) -> Unit,
    onEditPlacementClick: (String) -> Unit,
    onViewDiscussionClick: (String) -> Unit,
    onCreatePlacementClick: () -> Unit,
    onPlacementUpdated: () -> Unit,
    onPlacementCreated: () -> Unit
) {
    composable(route = PLACEMENTS_ROUTE) {
        PlacementsScreen(
            onPlacementClick = onPlacementClick,
            onCreatePlacementClick = onCreatePlacementClick
        )
    }

    composable(route = PLACEMENT_DETAILS_ROUTE) { backStackEntry ->
        val placementId = backStackEntry.arguments?.getString("placementId") ?: return@composable
        PlacementDetailsScreen(
            placementId = placementId,
            onBackClick = onBackClick,
            onViewDiscussionClick = {
                onViewDiscussionClick(placementId)
            },
            onEditClick = onEditPlacementClick
        )
    }

    composable(route = CREATE_PLACEMENT_ROUTE) {
        CreatePlacementScreen(
            onBackClick = onBackClick,
            onPlacementCreated = onPlacementCreated
        )
    }

    composable(route = EDIT_PLACEMENT_ROUTE) { backStackEntry ->
        val placementId = backStackEntry.arguments?.getString("placementId") ?: return@composable
        EditPlacementScreen(
            placementId = placementId,
            onBackClick = onBackClick,
            onPlacementUpdated = onPlacementUpdated
        )
    }
}
