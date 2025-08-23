package futur.apps.composeproject1._Mains

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.appScreens.navGraphs.AppNavGraph
import futur.apps.composeproject1.appScreens.scaffold.BottomNavigationBar
import futur.apps.composeproject1.appScreens.scaffold.FAB
import futur.apps.composeproject1.appScreens.scaffold.TopBar
import futur.apps.composeproject1.utils.Screen

/**
 * MainScreen is the root Composable that controls:
 * - Top AppBar
 * - Bottom Navigation Bar (only visible on specific routes)
 * - Floating Action Button (FAB) on selected routes
 * - Navigation Graph that defines all screen transitions
 */
@Composable
fun MainScreen() {

    // Setup Navigation Controller
    val navController = rememberNavController()

    // Get current route from navigation state
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Routes where BottomNavigationBar should be displayed
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Table.route,
        Screen.Settings.route,
        Screen.QuizPicker.route
        // Example: to support dynamic routes you can use baseRoute
        // Screen.QuizCategory.baseRoute
    )

    // Routes where FAB should be displayed
    val fabRoutes = listOf(Screen.Home.route)

    val showBottomBar = bottomBarRoutes.any { currentRoute?.startsWith(it) == true }
    val showFab = fabRoutes.any { currentRoute?.startsWith(it) == true }

    // Scaffold is the main UI container with slots for TopBar, BottomBar, FAB and content
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        floatingActionButton = { if (showFab) FAB() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Load the navigation graph (all app destinations are defined here)
            AppNavGraph(navController = navController, isUserLoggedIn = true)
            }
        }
}


/*@Composable
fun MainScreen(navController: NavHostController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // List of routes where BottomBar and FAB should appear
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Table.route,
        Screen.Settings.route,
        Screen.QuizPicker.route
    )
    val fabRoutes = listOf(Screen.Home.route)

    val showBottomBar = bottomBarRoutes.any { currentRoute?.startsWith(it) == true }
    val showFab = fabRoutes.any { currentRoute?.startsWith(it) == true }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        floatingActionButton = { if (showFab) FAB() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AppNavGraph(navController = navController, isUserLoggedIn = true)
        }
    }
}*/

