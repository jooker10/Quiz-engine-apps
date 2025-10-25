package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.utils.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val expanded = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val authViewModel: AuthViewModel = viewModel()

    val title = when {
        currentRoute == Screen.Home.route -> "Home"
        currentRoute == Screen.Stats.route -> "Statistics"
        currentRoute == Screen.Settings.route -> "Settings"
        currentRoute == Screen.UserCategory.route -> "User Category"
        currentRoute?.startsWith("quiz/") == true -> {
            val categoryName = backStackEntry?.arguments?.getString("category") ?: ""
            "$categoryName Quiz"
        }
        else -> "Home"
    }

    val showBackArrow = currentRoute?.startsWith("quiz/") == true

    // --- Sign-out Confirmation Dialog ---
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Sign out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    authViewModel.signOut()
                    // Optionally navigate back to login if needed
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainGraph.route) { inclusive = true }
                    }
                }) {
                    Text("Yes, Sign out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
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
                        onClick = {
                            expanded.value = false
                            // TODO: implement refresh if needed
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sign out") },
                        onClick = {
                            expanded.value = false
                            showDialog.value = true
                        }
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
        windowInsets = WindowInsets(0.dp)
    )
}
