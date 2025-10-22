package futur.apps.composeproject1.appScreens.navGraphs

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.navArgument
import futur.apps.composeproject1.appScreens._Screens.*
import futur.apps.composeproject1.quizsystem.ui.screens.QuizScreen
import futur.apps.composeproject1.quizCreator.CategoryListScreen
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizCategory
import futur.apps.composeproject1.viewmodels.QuizViewModel

/**
 * ============================================================
 * 🧭 AppNavGraph.kt
 *
 * 🔹 Purpose:
 * Root navigation graph controlling both:
 * - Main app flow (Home, Quiz, Settings, etc.)
 * - Auth flow (Login/Register)
 *
 * 🔹 Highlights:
 * - Uses nested graphs via `navigation()`
 * - Type-safe screen routes via [Screen]
 * - Supports multiple quiz modes (Default/Custom)
 * ============================================================
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    val userQuizViewModel: UserQuizViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn)
            Screen.MainGraph.route
        else
            Screen.AuthGraph.route
    ) {
        // -------------------- MAIN GRAPH --------------------
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController, userQuizViewModel)
        }

        // -------------------- AUTH GRAPH --------------------
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph()
        }
    }
}

/**
 * ============================================================
 * 📱 addMainGraph()
 *
 * Main user flow (Home, Stats, User Quizzes, Settings, Quiz)
 * ============================================================
 */
fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    userQuizViewModel: UserQuizViewModel
) {
    composable(Screen.Home.route) {
        HomeScreen(navController = navController)
    }

    composable(Screen.Stats.route) {
        StatsScreen()
    }

    composable(Screen.UserCategory.route) {
        CategoryListScreen(viewModel = userQuizViewModel)
    }

    // -------------------- QUIZ SCREEN --------------------
    composable(
        route = Screen.Quiz.route,
        arguments = listOf(
            navArgument("mode") { type = NavType.StringType },
            navArgument("categoryName") { type = NavType.StringType }
        )
    ) { backStackEntry ->

        val context = LocalContext.current
        val activity = context as? Activity
        val modeArg = backStackEntry.arguments?.getString("mode")
        val categoryName = backStackEntry.arguments?.getString("categoryName")

        // ✅ Parse mode safely
        val mode = modeArg?.let { runCatching { QuizMode.valueOf(it) }.getOrNull() }

        // ✅ Early return if invalid
        if (mode == null || categoryName.isNullOrEmpty() || activity == null) {
            navController.popBackStack()
            return@composable
        }

        val quizViewModel: QuizViewModel = hiltViewModel()

        // ✅ Determine quiz category type
        val category: QuizCategory? = when (mode) {
            QuizMode.DEFAULT -> {
                val default = runCatching { DefaultCategory.valueOf(categoryName) }.getOrNull()
                default?.let { QuizCategory.Default(it) }
            }
            QuizMode.CUSTOM -> QuizCategory.Custom(categoryName)
        }

        // ✅ Initialize the quiz directly
        if (category != null) {
            LaunchedEffect(categoryName, mode) {
                quizViewModel.initializeQuiz(category, mode)
            }

            QuizScreen(quizViewModel = quizViewModel)
        } else {
            navController.popBackStack()
        }
    }

    composable(Screen.Settings.route) {
        SettingsScreen()
    }
}

/**
 * ============================================================
 * 🔐 addAuthGraph()
 *
 * Handles login and register flows.
 * ============================================================
 */
fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
}
