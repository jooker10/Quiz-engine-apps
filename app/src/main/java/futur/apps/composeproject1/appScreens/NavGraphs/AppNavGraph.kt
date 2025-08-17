package futur.apps.composeproject1.appScreens.NavGraphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    quizViewModel: QuizViewModel,
    isUserLoggedIn: Boolean
) {

    val username = quizViewModel.username.collectAsState()
    val language = quizViewModel.langue.collectAsState()
    val isDarkTheme = quizViewModel.isDarkMode.collectAsState()



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
                quizViewModel.setNavigationBarVisibility(true)
                quizViewModel.setFabVisibility(true)
                HomeScreen()

            }
            composable(Screen.Table.route) {
                quizViewModel.setNavigationBarVisibility(true)
                quizViewModel.setFabVisibility(true)
                TableScreen()
            }
            // Quiz Navigation
            navigation(
                route = Screen.QuizRoot.route,
                startDestination = Screen.QuizPicker.route
            ) {
                composable(Screen.QuizPicker.route) {
                    quizViewModel.setNavigationBarVisibility(true)
                    quizViewModel.setFabVisibility(true)

                    PickerScreen{ categoryName ->
                       quizViewModel.setCategory(categoryName)
                        navController.navigate(Screen.QuizCategory.route + "/${categoryName.name}")
                    }

                }
                composable(
                    Screen.QuizCategory.route + "/{category}",
                    arguments = listOf(navArgument("category"){ type = NavType.StringType })
                    )
                { backStackEntry ->
                    val categoryArg = backStackEntry.arguments?.getString("category")

                    val category = try {
                        categoryArg?.let {
                            CategoryName.valueOf(it)
                        }
                    }
                    catch (e : IllegalArgumentException) { null }
                    if(category == null) {
                        Log.d("see", "category is null")
                        navController.popBackStack()
                    }
                    else {
                        quizViewModel.setNavigationBarVisibility(false)
                        quizViewModel.setFabVisibility(false)
                        QuizScreen(category = category)
                    }

                }
            }

            composable(Screen.Settings.route) {
                quizViewModel.setNavigationBarVisibility(true)
                quizViewModel.setFabVisibility(true)
                SettingsScreen(
                    isDarkTheme = isDarkTheme.value,
                    language = language.value,
                    userName = username.value,
                    onThemeChange = { quizViewModel.changeTheme(it) },
                    onUserNameChange = { quizViewModel.changeUserName(it) },
                    onLanguageChange = { quizViewModel.changeLanguage(it) },
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
