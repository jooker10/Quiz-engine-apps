package futur.apps.composeproject1.appScreens.NavGraphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.TableScreen
import futur.apps.composeproject1.appScreens._Screens.LoginScreen
import futur.apps.composeproject1.appScreens._Screens.QuizScreen
import futur.apps.composeproject1.appScreens._Screens.RegisterScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.appScreens.Screen
import futur.apps.composeproject1.appScreens._Screens.HomeScreen
import futur.apps.composeproject1._Mains.MainViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean
) {


    val username = viewModel.username.collectAsState()
    val language = viewModel.langue.collectAsState()
    val isDarkTheme = viewModel.isDarkMode.collectAsState()
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {

        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        )
        {
            composable(Screen.Home.route) {
                viewModel.setNavigationBarVisibility(true)
                viewModel.setFabVisibility(true)
                HomeScreen()

            }
            composable(Screen.Table.route) {
                viewModel.setNavigationBarVisibility(true)
                viewModel.setFabVisibility(true)
                TableScreen()
            }
            composable(Screen.Quiz.route) {
                viewModel.setNavigationBarVisibility(false)
                viewModel.setFabVisibility(false)
                QuizScreen()
            }
            composable(Screen.Settings.route) {
                viewModel.setNavigationBarVisibility(true)
                viewModel.setFabVisibility(true)
                SettingsScreen(
                    isDarkTheme = isDarkTheme.value,
                    language = language.value,
                    userName = username.value,
                    onThemeChange = { viewModel.changeTheme(it) },
                    onUserNameChange = { viewModel.changeUserName(it) },
                    onLanguageChange = { viewModel.changeLanguage(it) },
                    onPrivacyClick = {},
                    onContactClick = {}
                )
            }
        }

        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        )
        {
            authNavGraph(navController)
        }
    }


}

/*fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {

    composable(Screen.Home.route) {
        HomeScreen()

    }
    composable(Screen.Table.route) {
        EnglishTabsWithPager()
    }
    composable(Screen.Quiz.route) {
        QuizScreen()
    }
    composable(Screen.Settings.route) {
        SettingsScreen(
           isDarkTheme = isDarkTheme.value
        )
    }
}*/

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {

    composable(Screen.Login.route) {
        LoginScreen()
    }
    composable(Screen.Register.route) {
        RegisterScreen()
    }

}
