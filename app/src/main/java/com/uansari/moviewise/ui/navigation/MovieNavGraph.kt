package com.uansari.moviewise.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uansari.moviewise.ui.detail.DetailScreen
import com.uansari.moviewise.ui.home.HomeScreen
import com.uansari.moviewise.ui.search.SearchScreen
import com.uansari.moviewise.ui.watchlist.WatchlistScreen

/**
 * Central navigation graph.
 */
@Composable
fun MovieNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController, startDestination = Routes.HOME, modifier = modifier
    ) {

        // Home
        composable(route = Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { movieId ->
                    navController.navigate(Routes.detail(movieId))
                })
        }

        // Search
        composable(route = Routes.SEARCH) {
            SearchScreen(
                onNavigateToDetail = { movieId ->
                    navController.navigate(Routes.detail(movieId))
                })
        }

        // Watchlist
        composable(route = Routes.WATCHLIST) {
            WatchlistScreen(
                onNavigateToDetail = { movieId ->
                    navController.navigate(Routes.detail(movieId))
                })
        }

        // Detail
        composable(
            route = Routes.DETAIL, arguments = listOf(
                navArgument("movieId") { type = NavType.IntType })
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() })
        }
    }
}