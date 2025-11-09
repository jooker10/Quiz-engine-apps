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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.appScreens._Screens.LoadingScreen
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
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = BottomBarRoutes.any { currentRoute?.startsWith(it) == true }

    val authViewModel: AuthViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val userProfile by authViewModel.userProfile.collectAsState()
    val isChecking by authViewModel.isCheckingSession
    val homeUiState by homeViewModel.uiState.collectAsState()

    // 🧠 Unified global loading flag
    val isAppLoading = !isThemeReady || isChecking || homeUiState.isLoading

    // 🩵 Background color matches theme to hide flash
    val bgColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor) // hides the initial white flash
    ) {
        if (isAppLoading) {
            // 🕓 Show only the animated logo/text, nothing else yet
            LoadingScreen()
        } else {
            // ✅ Once everything’s ready, draw main UI
            Scaffold(
                topBar = { TopBar(navController = navController) },
                bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    AppNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        isUserLoggedIn = userProfile != null
                    )
                }
            }
        }
    }
}
