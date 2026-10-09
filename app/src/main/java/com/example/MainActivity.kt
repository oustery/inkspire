package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AiLabScreen
import com.example.ui.screens.BoardsScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.TryOnScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                InkspireApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Feed : Screen("feed", "Лента")
    object AiLab : Screen("ai_lab", "AI Студия")
    object TryOn : Screen("try_on/{tattooId}", "Примерка") {
        fun createRoute(tattooId: String) = "try_on/$tattooId"
    }
    object Detail : Screen("detail/{tattooId}", "Детали") {
        fun createRoute(tattooId: String) = "detail/$tattooId"
    }
    object Boards : Screen("boards", "Коллекции")
}

@Composable
fun InkspireApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar on primary tabs
    val showBottomBar = currentRoute in listOf(
        Screen.Feed.route,
        Screen.AiLab.route,
        Screen.Boards.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("inkspire_bottom_nav")
                ) {
                    // Feed Item
                    NavigationBarItem(
                        selected = currentRoute == Screen.Feed.route,
                        onClick = {
                            navController.navigate(Screen.Feed.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.GridView, contentDescription = "Лента") },
                        label = { Text("Лента", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // AI Studio Item
                    NavigationBarItem(
                        selected = currentRoute == Screen.AiLab.route,
                        onClick = {
                            navController.navigate(Screen.AiLab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Студия") },
                        label = { Text("AI Студия", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Try On shortcut
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(Screen.TryOn.createRoute("tat-1"))
                        },
                        icon = { Icon(Icons.Default.CenterFocusStrong, contentDescription = "Примерка") },
                        label = { Text("Примерка", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Boards / Saved Item
                    NavigationBarItem(
                        selected = currentRoute == Screen.Boards.route,
                        onClick = {
                            navController.navigate(Screen.Boards.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Коллекции") },
                        label = { Text("Доски", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen 1: Pinterest Feed
            composable(Screen.Feed.route) {
                FeedScreen(
                    onSelectTattoo = { tattooId ->
                        navController.navigate(Screen.Detail.createRoute(tattooId))
                    },
                    onTryOnTattoo = { tattooId ->
                        navController.navigate(Screen.TryOn.createRoute(tattooId))
                    },
                    onOpenAiLab = {
                        navController.navigate(Screen.AiLab.route)
                    }
                )
            }

            // Screen 2: Detail
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("tattooId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tattooId = backStackEntry.arguments?.getString("tattooId") ?: "tat-1"
                DetailScreen(
                    tattooId = tattooId,
                    onBack = { navController.popBackStack() },
                    onTryOn = { id ->
                        navController.navigate(Screen.TryOn.createRoute(id))
                    }
                )
            }

            // Screen 3: Dual-approach Try-On
            composable(
                route = Screen.TryOn.route,
                arguments = listOf(navArgument("tattooId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tattooId = backStackEntry.arguments?.getString("tattooId") ?: "tat-1"
                TryOnScreen(
                    tattooId = tattooId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Screen 4: AI Lab
            composable(Screen.AiLab.route) {
                AiLabScreen(
                    onBack = { navController.popBackStack() },
                    onTryOnGenerated = { generatedId ->
                        navController.navigate(Screen.TryOn.createRoute(generatedId))
                    }
                )
            }

            // Screen 5: Boards
            composable(Screen.Boards.route) {
                BoardsScreen(
                    onSelectTattoo = { tattooId ->
                        navController.navigate(Screen.Detail.createRoute(tattooId))
                    },
                    onTryOn = { tattooId ->
                        navController.navigate(Screen.TryOn.createRoute(tattooId))
                    }
                )
            }
        }
    }
}
