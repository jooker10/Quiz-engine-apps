package futur.apps.composeproject1._Mains

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.appScreens.NavGraphs.AppNavGraph
import futur.apps.composeproject1.appScreens.Scaffold.BottomNavigationBar
import futur.apps.composeproject1.appScreens.Scaffold.FAB
import futur.apps.composeproject1.appScreens.Scaffold.TopBar
import futur.apps.composeproject1.utils.Screen

@Composable
fun MainScreen(navController: NavHostController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // List of routes where BottomBar and FAB should appear
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Table.route,
        Screen.QuizPicker.route,
        Screen.QuizCategory.baseRoute // supports all QuizCategory/{param} routes
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
}
/*@Composable
fun MainScreen(quizViewModel: QuizViewModel) {
    val navController = rememberNavController()
    val showNavigationBar = quizViewModel.showNavigationBar
    val showFab = quizViewModel.showFab

    Scaffold(
        topBar = { TopBar(navController) },

        bottomBar = {
            if (showNavigationBar) BottomNavigationBar(navController = navController)
        },

        floatingActionButton = { if (showFab) FAB() },

        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )

            {
                AppNavGraph(
                    navController = navController,
                    quizViewModel = quizViewModel,
                    isUserLoggedIn = true
                )
            }
        }
    )

}*/

