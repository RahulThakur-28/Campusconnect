package com.rahul.campusconnect.presentation.bottomnavigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.announcement.navigation.announcementGraph
import com.rahul.campusconnect.presentation.announcement.screen.AnnouncementsScreen
import com.rahul.campusconnect.presentation.discussion.navigation.discussionGraph
import com.rahul.campusconnect.presentation.discussion.navigation.navigateToDiscussion
import com.rahul.campusconnect.presentation.event.navigation.eventGraph
import com.rahul.campusconnect.presentation.event.navigation.navigateToEventDetails
import com.rahul.campusconnect.presentation.event.screen.EventsScreen
import com.rahul.campusconnect.presentation.home.HomeScreen
import com.rahul.campusconnect.presentation.lostfound.navigation.lostFoundGraph
import com.rahul.campusconnect.presentation.lostfound.navigation.navigateToLostFoundDetails
import com.rahul.campusconnect.presentation.more.navigation.moreGraph
import com.rahul.campusconnect.presentation.more.screen.MoreScreen
import com.rahul.campusconnect.presentation.notes.navigation.navigateToNoteDetails
import com.rahul.campusconnect.presentation.notes.navigation.notesGraph
import com.rahul.campusconnect.presentation.notification.navigation.notificationGraph
import com.rahul.campusconnect.presentation.placement.navigation.navigateToEditPlacement
import com.rahul.campusconnect.presentation.placement.navigation.navigateToPlacementDetails
import com.rahul.campusconnect.presentation.placement.navigation.placementGraph
import com.rahul.campusconnect.presentation.placement.screen.PlacementsScreen
import com.rahul.campusconnect.presentation.profile.navigation.profileGraph
import com.rahul.campusconnect.presentation.search.navigation.searchGraph
import com.rahul.campusconnect.presentation.settings.navigation.settingsGraph
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Destinations where Paging Dashboard should be active
    val showDashboard = currentRoute == "dashboard" || currentRoute == null

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(if (showDashboard) PaddingValues() else innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            composable("dashboard") {
                DashboardPager(
                    navController = navController,
                    rootNavController = rootNavController
                )
            }

            // Other shared graphs and details (they will cover the bottom bar)
            eventGraph(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onViewDiscussionClick = { eventId ->
                    navController.navigateToDiscussion(eventId, DiscussionParentType.EVENT)
                }
            )

            placementGraph(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onPlacementClick = { id -> navController.navigateToPlacementDetails(id) },
                onEditPlacementClick = { id -> navController.navigateToEditPlacement(id) },
                onViewDiscussionClick = { placementId ->
                    navController.navigateToDiscussion(placementId, DiscussionParentType.PLACEMENT)
                },
                onCreatePlacementClick = { /* Handled in placements screen */ }
            )

            announcementGraph(navController = navController)
            moreGraph(navController = navController)
            notesGraph(navController)
            lostFoundGraph(navController)
            discussionGraph(navController)

            profileGraph(
                navController = navController,
                onLogoutClick = {
                    rootNavController.navigate(AppRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = { navController.navigate(AppRoutes.Settings.route) },
                onNoteClick = { noteId -> navController.navigateToNoteDetails(noteId) },
                onEventClick = { eventId -> navController.navigateToEventDetails(eventId) },
                onPlacementClick = { placementId -> navController.navigateToPlacementDetails(placementId) },
                onLostFoundClick = { itemId -> navController.navigateToLostFoundDetails(itemId) },
                onBackClick = { navController.popBackStack() }
            )

            settingsGraph(
                navController = navController,
                onEditProfileClick = { navController.navigate(AppRoutes.EditProfile.route) },
                onLogoutSuccess = {
                    rootNavController.navigate(AppRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )

            notificationGraph(navController)
            searchGraph(navController)
        }
    }
}

@Composable
fun DashboardPager(
    navController: NavHostController,
    rootNavController: NavController
) {
    val pagerState = rememberPagerState(pageCount = { bottomNavigationItems.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedIndex = pagerState.currentPage,
                onItemSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 2,
            userScrollEnabled = true
        ) { page ->
            when (bottomNavigationItems[page].route) {
                AppRoutes.Home.route -> HomeScreen(navController = navController)
                AppRoutes.Events.route -> EventsScreen(
                    onEventClick = { id -> navController.navigateToEventDetails(id) },
                    onCreateEventClick = { navController.navigate(AppRoutes.CreateEvent.route) },
                    navController = navController
                )
                AppRoutes.Placements.route -> PlacementsScreen(
                    onPlacementClick = { id -> navController.navigateToPlacementDetails(id) },
                    onCreatePlacementClick = { navController.navigate(AppRoutes.CreatePlacement.route) },
                    navController = navController
                )
                AppRoutes.Announcements.route -> AnnouncementsScreen(
                    onAnnouncementClick = { id -> navController.navigate("announcement_details/$id") },
                    onCreateAnnouncementClick = { navController.navigate(AppRoutes.CreateAnnouncement.route) },
                    navController = navController
                )
                AppRoutes.More.route -> MoreScreen(navController)
            }
        }
    }
}
