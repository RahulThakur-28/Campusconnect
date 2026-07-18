package com.rahul.campusconnect.presentation.lostfound.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.lostfound.screen.LostFoundDetailsScreen
import com.rahul.campusconnect.presentation.lostfound.screen.LostFoundScreen
import com.rahul.campusconnect.presentation.lostfound.screen.ReportLostFoundScreen

fun NavController.navigateToLostFound(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.LostFound.route, navOptions)
}

fun NavController.navigateToLostFoundDetails(itemId: String) {
    this.navigate("lost_found_details/$itemId")
}

fun NavController.navigateToReportLostFound() {
    this.navigate(AppRoutes.ReportLostFound.route)
}

fun NavGraphBuilder.lostFoundGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.LostFound.route) {
        LostFoundScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onItemClick = { itemId ->
                navController.navigateToLostFoundDetails(itemId)
            },
            onReportClick = {
                navController.navigateToReportLostFound()
            }
        )
    }

    composable(
        route = AppRoutes.LostFoundDetails.route,
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
        LostFoundDetailsScreen(
            itemId = itemId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable(route = AppRoutes.ReportLostFound.route) {
        ReportLostFoundScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onSubmitSuccess = {
                navController.popBackStack()
            }
        )
    }
}
