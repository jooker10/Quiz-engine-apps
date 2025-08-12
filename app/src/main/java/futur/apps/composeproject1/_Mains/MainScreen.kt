package futur.apps.composeproject1._Mains

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.AppScreens.NavGraphs.AppNavGraph
import futur.apps.composeproject1.AppScreens.Scaffold.BottomNavigationBar
import futur.apps.composeproject1.AppScreens.Scaffold.FAB
import futur.apps.composeproject1.AppScreens.Scaffold.TopBar
import futur.apps.composeproject1.DataStore.DataStoreViewModel
import futur.apps.composeproject1.QuizFiles.QuizViewModel

@Composable
fun MainScreen(dataStoreViewModel: DataStoreViewModel) {
    val navController = rememberNavController()
    val showNavigationBar = dataStoreViewModel.showNavigationBar
    val showFab = dataStoreViewModel.showFab
    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            if(showNavigationBar) BottomNavigationBar(navController = navController)
                    },
        floatingActionButton = { if (showFab) FAB() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )

            {
                AppNavGraph(
                    navController = navController,
                    viewModel = dataStoreViewModel,
                    isUserLoggedIn = true
                )
            }
        }
    )

}

