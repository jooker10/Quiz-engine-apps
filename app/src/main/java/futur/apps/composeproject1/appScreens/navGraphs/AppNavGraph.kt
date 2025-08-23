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
import futur.apps.composeproject1.appScreens.screens.TableScreen
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Screen


/**
 * Root Navigation Graph of the application.
 * Handles switching between:
 * - Auth graph (Login, Register)
 * - Main graph (Home, Table, Quiz, Settings)
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        // Main App Flow (Home, Table, Quiz, Settings)
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController)
        }

        // Authentication Flow (Login, Register)
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph()
        }
    }
}

/**
 * Main Graph: contains all the core features after login
 */
fun NavGraphBuilder.addMainGraph(navController: NavHostController) {

    // -------- Home Screen --------
    composable(Screen.Home.route) { HomeScreen() }

    // -------- Table Screen --------
    composable(Screen.Table.route) { TableScreen() }

    // -------- Quiz Flow (Picker + Quiz by Category) --------
    navigation(
        route = Screen.QuizRoot.route,
        startDestination = Screen.QuizPicker.route
    ) {
        // Quiz Category Picker
        composable(Screen.QuizPicker.route) {
            PickerScreen { quizCategory ->
                navController.navigate(Screen.QuizCategory.createRoute(quizCategory))
            }
        }

        // Quiz by Category
        composable(
            Screen.QuizCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")?.let {
                try {
                    Category.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            if (category == null) {
                // Defensive: if category is invalid, go back
                navController.popBackStack()
            } else {
                QuizScreen(category)
            }
        }
    }

    // -------- Settings Screen --------
    composable(Screen.Settings.route) {
        val context = LocalContext.current
        SettingsScreen(
            onPrivacyClick = {
                // Open Privacy Policy (example: Google.com)
                val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                context.startActivity(intent)
            },
            onContactClick = {
                // Send Email to Support
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:oulhajanouar@gmail.com".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
                }
                context.startActivity(emailIntent)
            }
        )
    }
}

/**
 * Auth Graph: contains Login and Register screens
 */
fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen()}
}
