package com.uansari.moviewise.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uansari.moviewise.ui.detail.DetailScreen
import com.uansari.moviewise.ui.home.HomeScreen

/**
 * Central navigation graph.
 */
@Composable
fun MovieNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController, startDestination = Routes.HOME
    ) {

        // Home
        composable(route = Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { movieId ->
                    navController.navigate(Routes.detail(movieId))
                })
        }

        // Detail
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}