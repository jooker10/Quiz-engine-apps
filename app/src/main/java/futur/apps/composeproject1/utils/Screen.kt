/*
package futur.apps.composeproject1.utils

*/
/**
 * Central navigation routes used across the app.
 * Each screen defines a unique lowercase route string.
 *//*

sealed class Screen(val route: String, val title: String = "") {

    // ---------------- Main Screens ----------------
    object Home : Screen("home", "Home")
    object Stats : Screen("stats", "Statistics")
    object Settings : Screen("settings", "Settings")
    object UserCategory : Screen("user_category", "Categories") // renamed from "about"

    // ---------------- Quiz ----------------
    object Quiz : Screen("quiz/{mode}/{categoryName}") {
        fun createRoute(mode: QuizMode, categoryName: String): String =
            "quiz/${mode.name}/$categoryName"
    }


    // Quiz Creator
    object AddQuestion : Screen("add_question/{categoryName}") {
        fun createRoute(categoryName: String) = "add_question/$categoryName"
    }

    object UserQuiz : Screen("user_quiz/{categoryName}") {
        fun createRoute(categoryName: String) = "user_quiz/$categoryName"
    }


    // ---------------- Authentication ----------------
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // ---------------- Root Graphs ----------------
    object MainGraph : Screen("main_graph")
    object AuthGraph : Screen("auth_graph")
}



*/
package futur.apps.composeproject1.utils

/**
 * Central navigation routes used across the app.
 * Each screen defines a unique lowercase route string.
 */
sealed class Screen(val route: String, val title: String = "") {

    // ---------------- Main Screens ----------------
    object Home : Screen("home", "Home")
    object Stats : Screen("stats", "Statistics")
    object Settings : Screen("settings", "Settings")
    object UserCategory : Screen("user_category", "Categories")

    // ---------------- Quiz ----------------
    /**
     * Unified Quiz route that supports both BuiltIn and UserCreated categories.
     * Example route: "quiz/BUILT_IN/Verbs" or "quiz/USER_CREATED/MyCustomCategory"
     */
    object Quiz : Screen("quiz/{mode}/{categoryName}") {
        fun createRoute(mode: QuizMode, categoryName: String): String =
            "quiz/${mode.name}/$categoryName"
    }

    // ---------------- Quiz Creator ----------------
    object AddQuestion : Screen("add_question/{categoryName}") {
        fun createRoute(categoryName: String) = "add_question/$categoryName"
    }

    // ---------------- Authentication ----------------
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // ---------------- Root Graphs ----------------
    object MainGraph : Screen("main_graph")
    object AuthGraph : Screen("auth_graph")
}
