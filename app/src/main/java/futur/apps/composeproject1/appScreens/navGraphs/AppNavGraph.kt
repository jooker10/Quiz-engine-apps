/*
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
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    // ✅ Use the new Room-based ViewModel
    val userQuizViewModel: UserQuizViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        // Main App Flow
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController, userQuizViewModel)
        }

        // Auth Flow
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph()
        }
    }
}

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

    // ---------------- User Categories ----------------
    composable(Screen.UserCategory.route) {
        CategoryListScreen(viewModel = userQuizViewModel)
    }

    // ---------------- Quiz ----------------
    composable(
        route = Screen.Quiz.route,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val context = LocalContext.current
        val category = backStackEntry.arguments?.getString("category")?.let {
            try {
                BuildInCategory.valueOf(it)
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

    // in addMainGraph
    composable(
        route = Screen.UserQuiz.route,
        arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
    ) { backStackEntry ->
        val categoryName = backStackEntry.arguments?.getString("categoryName")
        if (categoryName != null) {
            UserQuizScreen(categoryName = categoryName)
        } else {
            navController.popBackStack()
        }
    }


    // ---------------- Settings ----------------
    composable(Screen.Settings.route) {
        SettingsScreen()
    }
}

fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
}
*/
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
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.QuizCategory
import futur.apps.composeproject1.viewmodels.QuizViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    val userQuizViewModel: UserQuizViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        // Main App Flow
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            addMainGraph(navController, userQuizViewModel)
        }

        // Auth Flow
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            addAuthGraph()
        }
    }
}

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

    // ---------------- Unified Quiz Screen ----------------
    composable(
        route = Screen.Quiz.route,
        arguments = listOf(
            navArgument("mode") { type = NavType.StringType },
            navArgument("categoryName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val context = LocalContext.current
        val modeArg = backStackEntry.arguments?.getString("mode")
        val categoryName = backStackEntry.arguments?.getString("categoryName")

        val mode = modeArg?.let { runCatching { QuizMode.valueOf(it) }.getOrNull() }

        if (mode == null || categoryName.isNullOrEmpty()) {
            navController.popBackStack()
            return@composable
        }

        val quizViewModel: QuizViewModel = hiltViewModel()

        // Determine the type of category (BuiltIn or UserCreated)
        val category: QuizCategory? = when (mode) {
            QuizMode.BUILT_IN -> {
                val builtIn = runCatching { BuildInCategory.valueOf(categoryName) }.getOrNull()
                builtIn?.let { QuizCategory.BuiltIn(it) }
            }
            QuizMode.USER_CREATED -> QuizCategory.UserCreated(categoryName)
        }

        if (category != null) {
            LaunchedEffect(categoryName, mode) {
                quizViewModel.setCategory(context as Activity, category)
                quizViewModel.setLocalQuizMode(mode)
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

fun NavGraphBuilder.addAuthGraph() {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
}
