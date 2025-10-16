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
import androidx.navigation.navArgument
import androidx.navigation.navigation
import futur.apps.composeproject1.appScreens._Screens.*
import futur.apps.composeproject1.quizsystem.ui.screens.QuizScreen
import futur.apps.composeproject1.appScreens._Screens.SettingsScreen
import futur.apps.composeproject1.quizCreator.AddQuestionScreen
import futur.apps.composeproject1.quizCreator.CategoryListScreen
import futur.apps.composeproject1.quizCreator.QuizCreatorViewModel
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizViewModel

/**
 * Root Navigation Graph of the application.
 * Handles switching between:
 * - Auth graph (Login, Register)
 * - Main graph (Home, Stats, Quiz, Settings, UserCategory)
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
        // Main App Flow (Home, Stats, Quiz, Settings, UserCategory)
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

    composable(Screen.UserCategory.route) {
        val quizViewModel: QuizCreatorViewModel = hiltViewModel()

        CategoryListScreen(
            viewModel = quizViewModel,
            onAddQuestionClick = { categoryName ->
                navController.navigate(Screen.AddQuestion.createRoute(categoryName))
            }

        )
    }
    composable(
        route = Screen.AddQuestion.route,
        arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
    ) { backStackEntry ->
        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
        val quizViewModel: QuizCreatorViewModel = hiltViewModel()

        AddQuestionScreen(
            selectedCategory = categoryName,
            viewModel = quizViewModel,
            onQuestionSaved = {
                // Navigate back to UserCategory after saving the question
                navController.popBackStack()
            }
        )
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

            QuizScreen(quizViewModel = quizViewModel)
        }
    }

    // -------- Settings Screen --------
    composable(Screen.Settings.route) {
     //  val  themeViewModel: ThemeViewModel = hiltViewModel()
        SettingsScreen (
          /*  onLanguageSelected = {},
            onNotificationsToggle = {},
            onThemeChange = { themeViewModel.toggleTheme(it) },
            onPaletteChange = { themeViewModel.setPalette(it) },*/

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

