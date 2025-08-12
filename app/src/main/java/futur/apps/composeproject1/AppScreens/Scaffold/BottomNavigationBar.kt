package futur.apps.composeproject1.AppScreens.Scaffold

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.AppScreens.Screen
import futur.apps.composeproject1.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val items = listOf(Screen.Home, Screen.Table, Screen.Quiz, Screen.Settings)
    val unselectedIcons =
        listOf(
            painterResource(R.drawable.outline_home),
            painterResource(R.drawable.outline_table),
            painterResource(R.drawable.outline_quiz),
            painterResource(R.drawable.outline_settings)
        )
    val selectedIcons =
        listOf(
            painterResource(R.drawable.filled_home_24),
            painterResource(R.drawable.filled_table),
            painterResource(R.drawable.filled_quiz_24),
            painterResource(R.drawable.filled_settings)
        )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(6.dp))
            .shadow(8.dp)
            .height(56.dp)
    ) {
        items.forEachIndexed { index, screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                modifier = Modifier
                    .size((if (selected) 18.dp else 24.dp))
                    .scale(animateFloatAsState((if (selected) 1f else 0.8f)).value),

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)

                ),
                icon = {
                    Icon(
                        painter = if (selected) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = screen.route
                    )

                },
                label = { Text(screen.route) },
                alwaysShowLabel = false,
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }

                }
            )


        }
    }


}