package futur.apps.composeproject1.utils

sealed class Screen(val route: String, val title: String = "") {

    // ---------------- Main Screens ----------------
    object Home : Screen("home", "Home")
    object Stats : Screen("stats", "Stats")
    object Settings : Screen("settings", "Settings")
    object UserCategory : Screen("user_category", "Categories")
    object CategoryQuestions : Screen("questions/{categoryId}", "Questions") {
        fun createRoute(categoryId: Int) = "questions/$categoryId"
    }
    object Leaderboard : Screen("leaderboard", "Leaderboard")

    // ---------------- Quiz ----------------
    object Quiz : Screen("quiz/{mode}/{categoryName}") {
        fun createRoute(mode: QuizMode, categoryName: String): String =
            "quiz/${mode.name}/$categoryName"
    }

    // ---------------- Authentication ----------------
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // ---------------- Root Graphs ----------------
    object MainGraph : Screen("main_graph")
    object AuthGraph : Screen("auth_graph")
}
