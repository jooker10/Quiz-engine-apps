package futur.apps.composeproject1.appScreens.navGraphs

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.appScreens._Screens.LeaderboardScreen
import futur.apps.composeproject1.appScreens._Screens.QuizHomeScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.appScreens._Screens.StatsScreen
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.auth.LoginScreen
import futur.apps.composeproject1.auth.RegisterScreen
import futur.apps.composeproject1.quizCreator.CategoryListScreen
import futur.apps.composeproject1.quizsystem.ui.screens.QuizScreen
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizCategory
import futur.apps.composeproject1.viewmodels.QuizViewModel

/* ============================================================
   🌐 Unified AppNavGraph
   ------------------------------------------------------------
   Handles navigation for both Auth and Main areas.
   Works seamlessly with AuthViewModel state and the global loader.
   ============================================================ */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isUserLoggedIn: Boolean
) {
    val userQuizViewModel: UserQuizViewModel = hiltViewModel()
    val userProfile by authViewModel.userProfile.collectAsState()
    val isChecking by authViewModel.isCheckingSession

    // ✅ Navigate only when Auth state changes (after checking is done)
    LaunchedEffect(userProfile, isChecking) {
        if (!isChecking) {
            if (userProfile != null) {
                // User logged in → switch to Main graph
                navController.navigate(Screen.MainGraph.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            } else {
                // User logged out → switch to Auth graph
                navController.navigate(Screen.AuthGraph.route) {
                    popUpTo(Screen.MainGraph.route) { inclusive = true }
                }
            }
        }
    }

    // ✅ Define proper start destination (no Splash)
    val startDestination = if (userProfile != null || isUserLoggedIn)
        Screen.MainGraph.route
    else
        Screen.AuthGraph.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ---------------- MAIN GRAPH ----------------
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController, userQuizViewModel)
        }

        // ---------------- AUTH GRAPH ----------------
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph(navController, authViewModel)
        }
    }
}

/* ============================================================
   ✅ Main Graph
   ============================================================ */
fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    userQuizViewModel: UserQuizViewModel
) {
    composable(Screen.Home.route) {
        QuizHomeScreen(nav = navController)
    }

    composable(Screen.Stats.route) {
        StatsScreen()
    }

    composable(Screen.UserCategory.route) {
        CategoryListScreen(viewModel = userQuizViewModel)
    }

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

        val mode = modeArg?.let { runCatching { QuizMode.valueOf(it) }.getOrNull() }
        if (mode == null || categoryName.isNullOrEmpty() || activity == null) {
            navController.popBackStack()
            return@composable
        }

        val quizViewModel: QuizViewModel = hiltViewModel()
        val category: QuizCategory? = when (mode) {
            QuizMode.DEFAULT -> {
                val default = runCatching { DefaultCategory.valueOf(categoryName) }.getOrNull()
                default?.let { QuizCategory.Default(it) }
            }
            QuizMode.CUSTOM -> QuizCategory.Custom(categoryName)
        }

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
    composable(Screen.Leaderboard.route) {
        LeaderboardScreen()
    }
}

/* ============================================================
   ✅ Auth Graph
   ============================================================ */
fun NavGraphBuilder.addAuthGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    composable(Screen.Login.route) {
        LoginScreen(
            viewModel = authViewModel,
            onNavigateToRegister = { navController.navigate(Screen.Register.route) }
        )
    }

    composable(Screen.Register.route) {
        RegisterScreen(
            viewModel = authViewModel,
            onNavigateToLogin = { navController.popBackStack() }
        )
    }
}
