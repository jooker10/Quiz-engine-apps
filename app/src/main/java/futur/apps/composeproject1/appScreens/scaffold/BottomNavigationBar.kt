package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.R


/**
 * Professional BottomNavigationBar
 * - Correct selection even for nested graphs (QuizRoot)
 * - Safe navigation (singleTop + restoreState)
 * - Clean API with labels & icons
 */
data class NavItem(
    val label: String,
    /** Route to navigate to when clicked (must be a real destination, not just a graph route) */
    val navigateRoute: String,
    /** A predicate to decide if this item is selected for the current destination */
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
            label = "Table",
            navigateRoute = Screen.Table.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Table.route } == true },
            iconUnselected = R.drawable.outline_table,
            iconSelected = R.drawable.filled_table
        ),
        NavItem(
            label = "Quiz",
            navigateRoute = Screen.QuizPicker.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.QuizRoot.route } == true },
            iconUnselected = R.drawable.outline_quiz,
            iconSelected = R.drawable.filled_quiz_24
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

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(64.dp)
    ) {
        items.forEach { item ->
            val selected = item.isSelected(currentDestination)
            val scale by animateFloatAsState(if (selected) 1f else 0.92f, label = "nav-scale")

            NavigationBarItem(
                modifier = Modifier.scale(scale),
                icon = {
                    Icon(
                        painter = painterResource(if (selected) item.iconSelected else item.iconUnselected),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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

/*
data class NavItem(
    val screen: Screen,
    val label: String,
    val iconUnselected: Int,
    val iconSelected: Int
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val navItems = listOf(
        NavItem(Screen.Home, "Home", R.drawable.outline_home, R.drawable.filled_home_24),
        NavItem(Screen.Table, "Table", R.drawable.outline_table, R.drawable.filled_table),
        NavItem(Screen.QuizRoot, "Quiz", R.drawable.outline_quiz, R.drawable.filled_quiz_24),
        NavItem(Screen.Settings, "Settings", R.drawable.outline_settings, R.drawable.filled_settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(6.dp)
            .height(64.dp)
    ) {
        navItems.forEach { item ->
            val selected = currentRoute?.startsWith(item.screen.route) == true
            val scale by animateFloatAsState(if (selected) 1f else 0.9f, label = "scale")

            NavigationBarItem(
                modifier = Modifier.scale(scale),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) item.iconSelected else item.iconUnselected),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
            }
    }
}
*/

