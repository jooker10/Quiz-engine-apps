package futur.apps.composeproject1.appScreens.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.utils.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {

    val authViewModel: AuthViewModel = hiltViewModel()

    val userProfile by authViewModel.userProfile.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val mainRoutes = listOf(
        Screen.Home.route,
        Screen.Stats.route,
        Screen.Settings.route,
        Screen.UserCategory.route,
        Screen.Leaderboard.route
    )

    // هل نعرض سهم الرجوع؟
    val showBack = route !in mainRoutes

    // عنوان الصفحة
    val title = when (route) {
        Screen.Home.route -> "Home"
        Screen.Stats.route -> "Statistics"
        Screen.Settings.route -> "Settings"
        Screen.UserCategory.route -> "My Quizzes"
        Screen.Leaderboard.route -> "Leaderboard"
        Screen.About.route -> "About"
        Screen.Login.route -> "Sign In"
        Screen.Register.route -> "Register"
        else -> route?.substringAfterLast("/")?.replaceFirstChar { it.uppercase() } ?: ""
    }

    // Dialog: Sign Out
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Do you really want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes, Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(bottomEnd = 18.dp, bottomStart = 18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp)),
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (showBack) Alignment.Center else Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    visible = showBack,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text("Rate App") },
                            onClick = {
                                expanded = false
                                // TODO: open store
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                expanded = false
                                navController.navigate(Screen.About.route)
                            }
                        )

                        if (userProfile != null) {
                            DropdownMenuItem(
                                text = { Text("Sign Out") },
                                onClick = {
                                    expanded = false
                                    showSignOutDialog = true
                                }
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
