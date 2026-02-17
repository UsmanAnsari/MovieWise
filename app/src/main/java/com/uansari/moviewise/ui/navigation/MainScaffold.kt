package com.uansari.moviewise.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController()
) {
    // Observe current back stack entry to know which tab to highlight
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    /**
     * Determine if bottom nav should be visible.
     * Show it only when on one of the 3 root destinations.
     */
    val shouldShowBottomBar = BottomNavItem.items.any { item ->
        currentDestination?.route == item.route
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        /**
                         * hierarchy.any checks if the current destination
                         * or any parent destination matches this item's route.
                         * This handles nested navigation correctly.
                         */
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(selected = selected, onClick = {
                            /**
                             * Navigate to the tab's route.
                             *
                             * popUpTo(findStartDestination()) clears
                             * the back stack up to the start destination.
                             * This ensures tapping Home from Search doesn't
                             * create a back stack entry — Home becomes the
                             * only entry.
                             *
                             * launchSingleTop prevents duplicate entries
                             * if the user taps the same tab twice.
                             *
                             * restoreState restores the tab's state if it
                             * was previously visited (e.g. search results
                             * are still there when you return to Search).
                             */
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }, icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon
                                else item.unselectedIcon, contentDescription = item.title
                            )
                        }, label = { Text(text = item.title) })
                    }
                }
            }
        }) { paddingValues ->
        MovieNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}