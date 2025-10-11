package futur.apps.composeproject1.appScreens.navGraphs

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.*
import futur.apps.composeproject1.quizsystem.ui.screens.QuizMainScreen
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizViewModel

/**
 * Root Navigation Graph of the application.
 * Handles switching between:
 * - Auth graph (Login, Register)
 * - Main graph (Home, Stats, Quiz, Settings, About)
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
        // Main App Flow (Home, Stats, Quiz, Settings, About)
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController)
        }

        // Authentication Flow
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

    composable(Screen.Home.route) {
        HomeScreen(navController = navController)
    }

    composable(Screen.Stats.route) {
        StatsScreen()
    }

    composable(Screen.About.route) {
        AboutScreen()
    }

    // -------- Quiz Flow --------
    composable(
        route = Screen.Quiz.route,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val context = LocalContext.current
        val category = backStackEntry.arguments?.getString("category")?.let {
            try {
                Category.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        if (category == null) {
            navController.popBackStack()
        } else {
            val quizViewModel: QuizViewModel = hiltViewModel()

            LaunchedEffect(category) {
                quizViewModel.setCategory(context as Activity, category)
            }

            QuizMainScreen(quizViewModel = quizViewModel)
        }
    }

    // -------- Settings Screen --------
    composable(Screen.Settings.route) {
        val context = LocalContext.current
        SettingsScreen(
            onPrivacyClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                context.startActivity(intent)
            },
            onContactClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:oulhajanouar@gmail.com".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
                }
                context.startActivity(intent)
            }
        )
    }
}

/**
 * Auth Graph: contains Login and Register screens
 */
fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
}


/*
package futur.apps.composeproject1.appScreens.navGraphs

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.AboutScreen
import futur.apps.composeproject1.appScreens._Screens.HomeScreen
import futur.apps.composeproject1.appScreens._Screens.LoginScreen
import futur.apps.composeproject1.appScreens._Screens.RegisterScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.appScreens._Screens.StatsScreen
import futur.apps.composeproject1.quizsystem.ui.screens.QuizMainScreen
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.viewmodels.QuizViewModel


*/
/**
 * Root Navigation Graph of the application.
 * Handles switching between:
 * - Auth graph (Login, Register)
 * - Main graph (Home, Stats, Quiz, Settings)
 *//*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        // Main App Flow (Home, Stats, Quiz, Settings)
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

*/
/**
 * Main Graph: contains all the core features after login
 *//*

fun NavGraphBuilder.addMainGraph(navController: NavHostController) {

    // -------- Home Screen --------
    composable(Screen.Home.route) { HomeScreen(
        navController = navController

    ) }

    // -------- Stats Screen --------
    composable(Screen.Stats.route) {
        StatsScreen()
    }
    composable(Screen.About.route) {
       AboutScreen()
    }

    // -------- Quiz Flow (Picker + Quiz by Category) --------



        // Quiz by Category
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val category = backStackEntry.arguments?.getString("category")?.let {
                try {
                    Category.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            if (category == null) {
                // Invalid or missing category → go back to picker
                navController.popBackStack()
            } else {
                // ✅ Use Hilt to get the existing ViewModel
                val quizViewModel: QuizViewModel = hiltViewModel()

                // ✅ Start new quiz once when category changes
                LaunchedEffect(category) {
                    quizViewModel.setCategory(context as Activity, category)
                }

                // ✅ Show the quiz UI and allow going back
                QuizMainScreen(
                    quizViewModel = quizViewModel
                )
            }
        }


    // -------- Settings Screen --------
    composable(Screen.Settings.route) {
        val context = LocalContext.current
        SettingsScreen(
            onPrivacyClick = {
                // Open Privacy Policy (example)
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


*/
/**
 * Auth Graph: contains Login and Register screens
 *//*

fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen()}
}
*/
