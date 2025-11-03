package com.example.assignment5_3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavHostController

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { navController.navigate(Screen.Category.route) },
                modifier = modifier
            )
        }

        // Categories screen (shows all categories)
        composable(Screen.Category.route) {
            CategoriesScreen(
                onCategorySelected = { categoryName ->
                    navController.navigate(Screen.List.createRoute(categoryName))
                },
                modifier = modifier
            )
        }

        // List screen (receives category name as argument)
        composable(
            route = Screen.List.route + "/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            ListScreen(categoryName = categoryName, modifier = modifier) { detailId ->
                navController.navigate(Screen.Detail.createRoute(detailId))
            }
        }

        // Detail screen
        composable(
            route = Screen.Detail.route + "/{detailId}",
            arguments = listOf(navArgument("detailId") { type = NavType.IntType })
        ) { backStackEntry ->
            val detailId = backStackEntry.arguments?.getInt("detailId") ?: 0
            DetailScreen(detailId = detailId, modifier = Modifier)
        }
    }
}