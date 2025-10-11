package futur.apps.composeproject1.utils

/**
 * Central navigation routes used across the app.
 * Each screen defines a unique lowercase route string.
 */
sealed class Screen(val route: String, val title: String = "") {

    // Main Screens
    object Home : Screen("home", "Home")
    object Stats : Screen("stats", "Statistics")
    object Settings : Screen("settings", "Settings")
    object About : Screen("about", "About")

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


/*
package futur.apps.composeproject1.utils


    sealed class Screen(val route: String , val title: String = "") {

        // Home & Stats
        object Home : Screen("Home" , "Home")
        object Stats : Screen("Statistics", "Statistics")
        object Settings : Screen("Settings", "Settings")
        object About : Screen("About", "About")

        // Quiz
       // object QuizPicker : Screen("quiz_picker")
        object Quiz : Screen("Quiz/{category}") {
            fun createRoute(category: Category): String = "Quiz/${category.name}"

        }



        // Authentication
        object Login : Screen("Login", "Login")
        object Register : Screen("Register", "Register")

        // Graphs
        object MainGraph : Screen("main_graph")
        object AuthGraph : Screen("auth_graph")

    }
  */
/*  // Main
    object Home : Screen("Home")
    object Stats : Screen("Stats")
    object About : Screen("Quiz_root")
    object QuizPicker : Screen("Quiz_picker")
    object Quiz : Screen("Quiz_category")
    object Settings : Screen("Settings")

    // Auth
    object Login : Screen("Login")
    object Register : Screen("Register")

    object MainGraph : Screen("Main_Graph")
    object AuthGraph : Screen("Auth_Graph")*//*


*/
