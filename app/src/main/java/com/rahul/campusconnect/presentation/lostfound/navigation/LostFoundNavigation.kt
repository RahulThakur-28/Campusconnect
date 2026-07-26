package com.rahul.campusconnect.presentation.lostfound.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.lostfound.screen.*

fun NavController.navigateToLostFound(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.LostFound.route, navOptions)
}

fun NavController.navigateToLostFoundDetails(itemId: String) {
    this.navigate("lost_found_details/$itemId")
}

fun NavController.navigateToReportLostFound() {
    this.navigate(AppRoutes.ReportLostFound.route)
}

fun NavController.navigateToEditLostFound(itemId: String) {
    this.navigate("edit_lost_found/$itemId")
}

fun NavGraphBuilder.lostFoundGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.LostFound.route) {
        LostFoundScreen(
            onBackClick = { navController.popBackStack() },
            onItemClick = { itemId -> navController.navigateToLostFoundDetails(itemId) },
            onReportClick = { navController.navigateToReportLostFound() },
            navController = navController
        )
    }

    composable(
        route = AppRoutes.LostFoundDetails.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
        LostFoundDetailsScreen(
            itemId = itemId,
            onBackClick = { navController.popBackStack() },
            onEditClick = { id -> navController.navigateToEditLostFound(id) },
            navController = navController
        )
    }

    composable(route = AppRoutes.ReportLostFound.route) {
        ReportLostFoundScreen(
            onBackClick = { navController.popBackStack() },
            navController = navController
        )
    }

    composable(
        route = AppRoutes.EditLostFound.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
        EditLostFoundScreen(
            itemId = itemId,
            onBackClick = { navController.popBackStack() },
            navController = navController
        )
    }
}
