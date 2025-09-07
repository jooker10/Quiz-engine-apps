package com.anouar.myscreens.CustomComposable.bottomBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.R
import futur.apps.composeproject1.appScreens.scaffold.NavItem
import futur.apps.composeproject1.utils.Screen

// Represent all items in the bottom bar
sealed class BottomBarEntry {
    data class Tab(val items : List<NavItem>) : BottomBarEntry()
    object Fab : BottomBarEntry() // middle add button
}

@Composable
fun YoutubeStyleBottomAppBar(
    navController: NavHostController,
  /*  selectedItem: Int,
    onItemSelected: (Int) -> Unit,*/
    onFabClick: () -> Unit
) {
    // Ordered list: tabs → fab → tabs


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
            iconUnselected = futur.apps.composeproject1.R.drawable.outline_quiz,
            iconSelected = futur.apps.composeproject1.R.drawable.filled_quiz_24
        ),
        NavItem(
            label = "Settings",
            navigateRoute = Screen.Settings.route,
            isSelected = { dest -> dest?.hierarchy?.any { it.route == Screen.Settings.route } == true },
            iconUnselected = futur.apps.composeproject1.R.drawable.outline_settings,
            iconSelected = R.drawable.filled_settings
        )
    )

    val entries = listOf(
        BottomBarEntry.Tab(listOf(items[0])),
        BottomBarEntry.Tab(listOf(items[1])),
        BottomBarEntry.Fab,
        BottomBarEntry.Tab(listOf(items[2])),
        BottomBarEntry.Tab(listOf(items[3])),
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        entries.forEachIndexed { index, entry ->

            val selectedItem = entries.indexOfFirst { it is BottomBarEntry.Tab && it.items[0].isSelected(currentDestination) }

            when (entry) {
                is BottomBarEntry.Tab -> {
                    val item = entry.items[0]
                    NavigationBarItem(
                        selected = item.isSelected(currentDestination),
                        onClick = {
                            navController.navigate(item.navigateRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (item.isSelected(currentDestination)) item.iconSelected else item.iconUnselected
                                ),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }

                /*is BottomBarEntry.Tab -> {
                    NavigationBarItem(
                        selected = entry.items[index].isSelected(currentDestination),
                        onClick = {
                            navController.navigate(entry.items[index].navigateRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (selectedItem == index) entry.items[index].iconSelected else entry.items[index].iconUnselected
                                ),
                                contentDescription = entry.items[index].label
                            )
                        },
                        label = { Text(entry.items[index].label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }*/

                BottomBarEntry.Fab -> {
                    NavigationBarItem(
                        selected = false,
                        onClick = onFabClick,
                        icon = {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 6.dp,
                                onClick = onFabClick
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        },
                        label = { }, // no label for FAB
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

