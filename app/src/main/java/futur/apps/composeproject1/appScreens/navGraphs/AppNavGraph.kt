package futur.apps.composeproject1.appScreens.navGraphs

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.appScreens._Screens.*
import futur.apps.composeproject1.auth.*
import futur.apps.composeproject1.quiz.ui.screens.QuizScreen
import futur.apps.composeproject1.quizCreator.CategoryListScreen
import futur.apps.composeproject1.quizCreator.CategoryQuestionsScreen
import futur.apps.composeproject1.utils.*
import futur.apps.composeproject1.viewmodels.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val userQuizViewModel: UserQuizViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // MAIN
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController, userQuizViewModel)
        }

        // AUTH
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph(navController, authViewModel)
        }
    }
}


fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    userQuizViewModel: UserQuizViewModel
) {
    composable(Screen.Home.route) {
        QuizHomeScreen(nav = navController)
    }
    composable(Screen.Stats.route) { StatsScreen() }
    composable(Screen.Settings.route) { SettingsScreen() }
    composable(Screen.Leaderboard.route) { LeaderboardScreen() }

    // ---------- User Quiz Creator ----------
    composable(Screen.UserCategory.route) {
        CategoryListScreen(navController = navController, viewModel = userQuizViewModel)
    }

    composable(Screen.About.route) {
        AboutScreen()
    }

    composable(
        route = Screen.CategoryQuestions.route,
        arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
        CategoryQuestionsScreen(
            categoryId = categoryId,
            navController = navController,
            viewModel = userQuizViewModel
        )
    }

    // ---------- Quiz ----------
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
                val d = runCatching { DefaultCategory.valueOf(categoryName) }.getOrNull()
                d?.let { QuizCategory.Default(it) }
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
}

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
