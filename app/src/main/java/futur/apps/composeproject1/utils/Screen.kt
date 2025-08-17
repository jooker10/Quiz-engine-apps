package futur.apps.composeproject1.utils

sealed class Screen(val route : String)
{
    // Main
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
    object AuthGraph : Screen("Auth_Graph")

}