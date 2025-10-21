package futur.apps.composeproject1._Mains

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.appScreens.navGraphs.AppNavGraph
import futur.apps.composeproject1.appScreens.scaffold.MainNavigation
import futur.apps.composeproject1.appScreens.scaffold.TopBar
import futur.apps.composeproject1.utils.Screen

// Define routes that control bottom bar and top bar visibility
private val BottomBarRoutes = listOf(
    Screen.Home.route,
    Screen.Stats.route,
    Screen.Settings.route,
    Screen.UserCategory.route
)

private val TopBarRoutes = listOf(
    Screen.Home.route,
    Screen.Settings.route,
    Screen.Stats.route,
    Screen.UserCategory.route
)

/**
 * MainScreen is the root composable hosting:
 * - Top App Bar (optional)
 * - Bottom Navigation Bar (optional)
 * - Navigation Graph with all app destinations
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = BottomBarRoutes.any { currentRoute?.startsWith(it) == true }
    //val showTopBar = TopBarRoutes.any { currentRoute?.startsWith(it) == true }

    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = {
            if (showBottomBar) {
                MainNavigation(navController = navController)

            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AppNavGraph(
                navController = navController,
                isUserLoggedIn = true
            )
        }
    }
}
