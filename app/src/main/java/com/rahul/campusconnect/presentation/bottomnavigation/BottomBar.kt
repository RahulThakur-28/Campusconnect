package com.rahul.campusconnect.presentation.bottomnavigation


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController
) {

    // Observe the current navigation destination.
    // Whenever the route changes, this composable automatically recomposes.
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Get the route of the currently visible screen.
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {

        bottomNavigationItems.forEach { item ->

            // Check whether this bottom navigation item
            // represents the currently selected destination.
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,

                onClick = {

                    // Avoid navigating again if the user
                    // taps the already selected tab.
                    if (!isSelected) {

                        navController.navigate(item.route) {

                            // Keep only one instance of each
                            // top-level destination.
                            launchSingleTop = true

                            // Restore the previous state of the tab.
                            restoreState = true

                            // Save the state when switching tabs.
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.onSecondaryContainer,

                    selectedTextColor =
                        MaterialTheme.colorScheme.onSurface,

                    indicatorColor =
                        MaterialTheme.colorScheme.secondaryContainer,

                    unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,

                    unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}