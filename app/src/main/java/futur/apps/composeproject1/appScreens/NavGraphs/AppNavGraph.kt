package futur.apps.composeproject1.appScreens.NavGraphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.TableScreen
import futur.apps.composeproject1.appScreens._Screens.LoginScreen
import futur.apps.composeproject1.appScreens._Screens.RegisterScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.appScreens._Screens.HomeScreen
import futur.apps.composeproject1._Mains.QuizViewModel
import futur.apps.composeproject1.appScreens._Screens.PickerScreen
import futur.apps.composeproject1.appScreens._Screens.QuizScreen
import futur.apps.composeproject1.utils.CategoryName


@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: QuizViewModel,
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
            navigation(
                route = Screen.QuizRoot.route,
                startDestination = Screen.QuizPicker.route
            ) {
                composable(Screen.QuizPicker.route) {
                    viewModel.setNavigationBarVisibility(true)
                    viewModel.setFabVisibility(true)
                    PickerScreen{ categoryName ->
                        navController.navigate(Screen.QuizCategory.route + "/${categoryName.displayName}")
                    }

                }
                composable(
                    Screen.QuizCategory.route + "/{category}",
                    arguments = listOf(navArgument("category"){ type = NavType.StringType })
                    )
                { backStackEntry ->
                    val categoryName = backStackEntry.arguments?.getString("category")
                    val category = CategoryName.fromDisplayName(categoryName)
                    viewModel.setNavigationBarVisibility(false)
                    viewModel.setFabVisibility(false)
                    viewModel.setCategory(category)
                    QuizScreen(category = category)
                }
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
