package futur.apps.composeproject1.AppScreens

sealed class Screen(val route : String)
{
    // Main
    object Home : Screen("Home")
    object Table : Screen("Table")
    object Quiz : Screen("Quiz")
    object Settings : Screen("Settings")

    // Auth
    object Login : Screen("Login")
    object Register : Screen("Register")

    object MainGraph : Screen("Main_Graph")
    object AuthGraph : Screen("Auth_Graph")

}