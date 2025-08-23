package futur.apps.composeproject1.utils


    sealed class Screen(val route: String) {

        // Home & Table
        object Home : Screen("home")
        object Table : Screen("table")

        // Quiz
        object QuizPicker : Screen("quiz_picker")
        object QuizCategory : Screen("quiz_category/{category}") {
            fun createRoute(category: Category): String = "quiz_category/${category.name}"
            val baseRoute: String = "quiz_category"
        }

        // Settings
        object Settings : Screen("settings")

        // Authentication
        object Login : Screen("login")
        object Register : Screen("register")

        // Graphs
        object MainGraph : Screen("main_graph")
        object AuthGraph : Screen("auth_graph")
        object QuizRoot : Screen("quiz_root")
    }
  /*  // Main
    object Home : Screen("Home")
    object Table : Screen("Table")
    object QuizRoot : Screen("Quiz_root")
    object QuizPicker : Screen("Quiz_picker")
    object QuizCategory : Screen("Quiz_category")
    object Settings : Screen("Settings")

    // Auth
    object Login : Screen("Login")
    object Register : Screen("Register")

    object MainGraph : Screen("Main_Graph")
    object AuthGraph : Screen("Auth_Graph")*/

