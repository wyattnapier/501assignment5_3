package com.example.assignment5_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment5_3.ui.theme.Assignment5_3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment5_3Theme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Category : Screen("category", "Categories", Icons.Default.Share)
    object List : Screen("list", "List", Icons.Default.List) {
        fun createRoute(categoryName: String) = "list/$categoryName"
    }
    object Detail : Screen("detail", "Detail", Icons.Default.Place) {
        fun createRoute(categoryName: String, detailId: Int) = "detail/$categoryName/$detailId"
    }

}

val screens = listOf(
    Screen.Home,
    Screen.Category,
    Screen.List,
    Screen.Detail
)

val bottomBarScreens = listOf(
    Screen.Home,
)


@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {TopBarComponent()},
        bottomBar = {BottomBarComponent(navController = navController)}
    )
    { innerPadding ->
        AppNavGraph(Modifier.padding(innerPadding), navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(){
    TopAppBar(
        title = {Text("Explore Boston")},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun BottomBarComponent(navController: NavHostController){
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val currentRoute = currentDestination?.route
        val isRootScreen = bottomBarScreens.any { it.route == currentRoute }

        bottomBarScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    // This is the core navigation logic.
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true // Save the state of the screen you're leaving.
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item.
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item.
                        restoreState = true
                    }
                }
            )
        }
        // adds back arrow when not on home screen
         if(!isRootScreen) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.ArrowBack, contentDescription = "Back") },
                label = { Text("Back") },
                selected = false,
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HomeScreen(onCategoryClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Welcome to Boston!")
        Button(onClick = onCategoryClick) {
            Text("Browse Categories")
        }
    }
}

@Composable
fun CategoriesScreen(onCategorySelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val categories = listOf("Museums", "Parks", "Restaurants") // your categories

    Column(modifier = modifier) {
        Text("Categories", style = MaterialTheme.typography.headlineMedium)
        categories.forEach { category ->
            Button(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(category)
            }
        }
    }
}

@Composable
fun ListScreen(categoryName: String, modifier: Modifier = Modifier, onDetailClick: (String, Int) -> Unit) {
    val items = when(categoryName) {
        "Museums" -> listOf("MIT Museum", "Museum of Science")
        "Parks" -> listOf("Boston Common", "Public Garden")
        "Restaurants" -> listOf("Legal Sea Foods", "Union Oyster House")
        else -> emptyList()
    }

    Column (modifier = modifier ){
        Text("Category: $categoryName", style = MaterialTheme.typography.headlineSmall)
        items.forEachIndexed { index, item ->
            Button(
                onClick = { onDetailClick(categoryName, index) },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(item)
            }
        }
    }
}

@Composable
fun DetailScreen(categoryName: String, detailId: Int, modifier: Modifier = Modifier) {
    val detailsMap = mapOf(
        "Museums" to listOf(
            "MIT Museum" to "A museum showcasing technology, innovation, and art from MIT’s rich history.",
            "Museum of Science" to "Interactive exhibits on science, nature, and technology. Great for families."
        ),
        "Parks" to listOf(
            "Boston Common" to "America’s oldest public park, a beautiful green space in the heart of the city.",
            "Public Garden" to "Famous for its swan boats, lush flowers, and peaceful atmosphere."
        ),
        "Restaurants" to listOf(
            "Legal Sea Foods" to "A Boston classic known for its fresh local seafood and chowder.",
            "Union Oyster House" to "The oldest continuously operating restaurant in the U.S., serving oysters and history."
        )
    )

    val details = detailsMap[categoryName]
    val (title, description) = details?.getOrNull(detailId)
        ?: ("Unknown Place" to "No description available.")

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Category: $categoryName", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}