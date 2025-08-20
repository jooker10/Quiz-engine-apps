package futur.apps.composeproject1.appScreens.navGraphs

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.HomeScreen
import futur.apps.composeproject1.appScreens._Screens.LoginScreen
import futur.apps.composeproject1.appScreens._Screens.PickerScreen
import futur.apps.composeproject1.appScreens._Screens.QuizScreen
import futur.apps.composeproject1.appScreens._Screens.RegisterScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.appScreens._Screens.TableScreen
import futur.apps.composeproject1.utils.CategoryName
import futur.apps.composeproject1.utils.Screen

@Composable
fun AppNavGraph(navController: NavHostController, isUserLoggedIn: Boolean) {

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        // Main feature graph
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) { mainNavGraph(navController) }

        // Authentication graph
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) { authNavGraph() }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {

    composable(Screen.Home.route) { HomeScreen() }

    composable(Screen.Table.route) { TableScreen() }

    navigation(
        route = Screen.QuizRoot.route,
        startDestination = Screen.QuizPicker.route
    ) {
        composable(Screen.QuizPicker.route) {
            PickerScreen { categoryName ->
                navController.navigate(Screen.QuizCategory.createRoute(categoryName))
            }
        }
        composable(
            Screen.QuizCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")?.let {
                try {
                    CategoryName.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            if (category == null) navController.popBackStack()
            else QuizScreen(category)
        }
    }

    composable(Screen.Settings.route) {
        val context = LocalContext.current
        SettingsScreen(
            onPrivacyClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                context.startActivity(intent)
            },
            onContactClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:oulhajanouar@gmail.com".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
                }
                context.startActivity(emailIntent)
            }
        )
    }
}

fun NavGraphBuilder.authNavGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
}
