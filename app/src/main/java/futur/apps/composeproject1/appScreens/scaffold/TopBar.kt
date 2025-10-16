package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.utils.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val expanded = remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Determine title dynamically
    val title = when {
        currentRoute == Screen.Home.route -> "Home"
        currentRoute == Screen.Stats.route -> "Statistics"
        currentRoute == Screen.Settings.route -> "Settings"
        currentRoute == Screen.UserCategory.route -> "UserCategory"
        currentRoute?.startsWith("quiz/") == true -> {
            // Extract category from route
            val categoryName = backStackEntry?.arguments?.getString("category") ?: ""
            "$categoryName Quiz"
        }
        else -> "Home"
    }

    // Show back arrow only on Quiz screen
    val showBackArrow = currentRoute?.startsWith("quiz/") == true

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .windowInsetsPadding(WindowInsets.statusBars)
            .clip(RoundedCornerShape(12.dp)),
        title = {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = if (showBackArrow) Alignment.Center else Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            Box {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Refresh") },
                        onClick = { expanded.value = false /* TODO */ }
                    )
                    DropdownMenuItem(
                        text = { Text("Build") },
                        onClick = { expanded.value = false /* TODO */ }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        windowInsets = WindowInsets(0.dp) // avoid double padding
    )
}
