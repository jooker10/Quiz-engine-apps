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
    object UserCategory : Screen("user_category", "Categories") // renamed from "about"

    // ---------------- Quiz ----------------
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: Category): String = "quiz/${category.name}"
    }

    // Quiz Creator
    object AddQuestion : Screen("add_question/{categoryName}", "Add Question") {
        fun createRoute(categoryName: String): String = "add_question/$categoryName"
    }

    // ---------------- Authentication ----------------
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // ---------------- Root Graphs ----------------
    object MainGraph : Screen("main_graph")
    object AuthGraph : Screen("auth_graph")
}



/*
package futur.apps.composeproject1.utils

*/
/**
 * Central navigation routes used across the app.
 * Each screen defines a unique lowercase route string.
 *//*

sealed class Screen(val route: String, val title: String = "") {

    // Main Screens
    object Home : Screen("home", "Home")
    object Stats : Screen("stats", "Statistics")
    object Settings : Screen("settings", "Settings")
    object UserCategory : Screen("about", "UserCategory")

    // Quiz
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: Category): String = "quiz/${category.name}"
    }

    // Authentication
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // Root Graphs
    object MainGraph : Screen("main_graph")
    object AuthGraph : Screen("auth_graph")
}
*/
