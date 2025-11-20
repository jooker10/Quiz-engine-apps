package futur.apps.composeproject1._Mains

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.appScreens._Screens.AppLoadingSplash
import futur.apps.composeproject1.appScreens.navGraphs.AppNavGraph
import futur.apps.composeproject1.appScreens.scaffold.BottomNavigationBar
import futur.apps.composeproject1.appScreens.scaffold.TopBar
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.HomeViewModel

private val BottomBarRoutes = listOf(
    Screen.Home.route,
    Screen.Stats.route,
    Screen.Settings.route,
    Screen.UserCategory.route,
    Screen.Leaderboard.route
)

@Composable
fun MainScreen(isThemeReady: Boolean) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userProfile by authViewModel.userProfile.collectAsState()
    val isChecking by authViewModel.isCheckingSession

    // 🚫 لا تنشئ NavController قبل انتهاء Session Check
    if (!isThemeReady || isChecking) {
        AppLoadingSplash()
        return
    }

    // 🚀 الآن فقط ننشئ NavController (بعد انتهاء الفحص بالكامل)
    val navController = rememberNavController()

    val startDestination =
        if (userProfile != null) Screen.MainGraph.route
        else Screen.AuthGraph.route


    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { if (shouldShowBottomBar(navController)) BottomNavigationBar(navController) }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                startDestination = startDestination
            )
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val routes = listOf(
        Screen.Home.route,
        Screen.Stats.route,
        Screen.Settings.route,
        Screen.UserCategory.route,
        Screen.Leaderboard.route
    )
    return routes.any { currentRoute?.startsWith(it) == true }
}

