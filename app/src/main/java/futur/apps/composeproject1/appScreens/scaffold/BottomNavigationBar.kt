package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.R
import futur.apps.composeproject1.utils.Screen

data class NavItem(
    val label: String,
    val navigateRoute: String,
    val isSelected: (NavDestination?) -> Boolean,
    val iconUnselected: Int,
    val iconSelected: Int
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem(
            label = "Home",
            navigateRoute = Screen.Home.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Home.route } == true },
            iconUnselected = R.drawable.outline_home,
            iconSelected = R.drawable.filled_home_24
        ),
        NavItem(
            label = "Stats",
            navigateRoute = Screen.Stats.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Stats.route } == true },
            iconUnselected = R.drawable.outline_table,
            iconSelected = R.drawable.filled_table
        ),
        NavItem(
            label = "My Quizzes",
            navigateRoute = Screen.UserCategory.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.UserCategory.route } == true },
            iconUnselected = R.drawable.about_outline,
            iconSelected = R.drawable.about_filled
        ),
        NavItem(
            label = "Top 10",
            navigateRoute = Screen.Leaderboard.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Leaderboard.route } == true },
            iconUnselected = R.drawable.outline_menu,
            iconSelected = R.drawable.outline_menu
        ),
        NavItem(
            label = "Settings",
            navigateRoute = Screen.Settings.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Settings.route } == true },
            iconUnselected = R.drawable.outline_settings,
            iconSelected = R.drawable.filled_settings
        )
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val firstFrame = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // after first composition, disable instant appearance flag
        firstFrame.value = false
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation,
        modifier = Modifier
            .navigationBarsPadding()
            .height(70.dp)
    ) {
        items.forEach { item ->
            val selected = item.isSelected(currentDestination)
            val scale by animateFloatAsState(
                targetValue = if (firstFrame.value) 1f else if (selected) 1f else 0.92f,
                label = "nav-scale"
            )

            NavigationBarItem(
                modifier = Modifier.scale(scale),
                icon = {
                    Icon(
                        painter = painterResource(
                            if (selected) item.iconSelected else item.iconUnselected
                        ),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = {
                    navController.navigate(item.navigateRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
