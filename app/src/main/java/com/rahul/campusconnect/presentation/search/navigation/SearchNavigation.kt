package com.rahul.campusconnect.presentation.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.rahul.campusconnect.domain.model.SearchResultType
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.announcement.navigation.navigateToAnnouncementDetails
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.search.screen.SearchScreen

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Search.route, navOptions)
}

fun NavGraphBuilder.searchGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.Search.route) {
        SearchScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onResultClick = { result ->
                when (result.type) {
                    SearchResultType.ANNOUNCEMENT -> navController.navigateToAnnouncementDetails(result.id)
                    SearchResultType.EVENT -> navController.navigateToEventDetails(result.id)
                    SearchResultType.PLACEMENT -> navController.navigateToPlacementDetails(result.id)
                    SearchResultType.NOTE -> navController.navigateToNoteDetails(result.id)
                    SearchResultType.LOST_FOUND -> navController.navigateToLostFoundDetails(result.id)
                }
            }
        )
    }
}
