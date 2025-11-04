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
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
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
            route = "list/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            ListScreen(
                categoryName = categoryName,
                onDetailClick = { category, detailId ->
                    navController.navigate(Screen.Detail.createRoute(category, detailId))
                }
            )
        }

        // Detail screen
        composable(
            route = "detail/{categoryName}/{detailId}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("detailId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val detailId = backStackEntry.arguments?.getInt("detailId") ?: 0
            DetailScreen(categoryName = categoryName, detailId = detailId, modifier)
        }
    }
}