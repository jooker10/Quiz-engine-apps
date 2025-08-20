package futur.apps.composeproject1._Mains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ui.theme.ComposeProject1Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val quizViewModel: QuizViewModel = hiltViewModel()
            val isDarkTheme = quizViewModel.isDarkMode.collectAsState()
            val navController = rememberNavController()
            ComposeProject1Theme(isDarkTheme.value) {
                MainScreen(navController = navController)
            }
        }
    }
}
