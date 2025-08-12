package futur.apps.composeproject1._Mains

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import futur.apps.composeproject1.appScreens.NavGraphs.AppNavGraph
import futur.apps.composeproject1.appScreens.Scaffold.BottomNavigationBar
import futur.apps.composeproject1.appScreens.Scaffold.FAB
import futur.apps.composeproject1.appScreens.Scaffold.TopBar

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val showNavigationBar = mainViewModel.showNavigationBar
    val showFab = mainViewModel.showFab
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
                    viewModel = mainViewModel,
                    isUserLoggedIn = true
                )
            }
        }
    )

}

