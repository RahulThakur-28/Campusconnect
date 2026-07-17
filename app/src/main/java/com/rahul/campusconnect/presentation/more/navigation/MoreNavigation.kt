package com.rahul.campusconnect.presentation.more.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.more.screen.MoreScreen

fun NavController.navigateToMore(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.More.route, navOptions)
}

fun NavGraphBuilder.moreGraph(
    navController: NavController
) {
    composable(route = AppRoutes.More.route) {
        MoreScreen(navController = navController)
    }
}
