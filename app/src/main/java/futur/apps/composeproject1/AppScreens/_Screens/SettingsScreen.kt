package futur.apps.composeproject1.AppScreens._Screens

import futur.apps.composeproject1.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    userName: String,
    language: String,
    isDarkTheme: Boolean,
    onUserNameChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (Boolean) -> Unit,
    onPrivacyClick: () -> Unit,
    onContactClick: () -> Unit
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var themeState by remember { mutableStateOf(isDarkTheme) }
    var languageState by remember { mutableStateOf(language) }
    var nameState by remember { mutableStateOf(userName) }

    val languages = listOf("English", "French", "Spanish", "Arabic")

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            // User Name
            item {
                ListItem(
                    headlineContent = { Text("Username") },
                    supportingContent = { Text(nameState) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.clickable { showNameDialog = true }
                )
            }

            // Language
            item {
                ListItem(
                    headlineContent = { Text("Language") },
                    supportingContent = { Text(languageState) },
                    leadingContent = { Icon(painterResource(R.drawable.outline_language), contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier.clickable { showLanguageMenu = true }
                )
            }

            // Theme Switch
            item {
                ListItem(
                    headlineContent = { Text("Dark Theme") },
                    leadingContent = { Icon(painterResource(R.drawable.outline_theme), contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = themeState,
                            onCheckedChange = { newValue ->
                                themeState = newValue
                                onThemeChange(newValue) }
                        )
                    }
                )
            }

            // Privacy Policy Button
            item {
                OutlinedButton(
                    onClick = onPrivacyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(painterResource(R.drawable.outline_policy), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Privacy Policy")
                }
            }

            // Contact Us Button
            item {
                ElevatedButton(
                    onClick = onContactClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Contact Us")
                }
            }
        }

        // Username Dialog
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("Change Username") },
                text = {
                    TextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Username") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        nameState = tempName
                        onUserNameChange(tempName)
                        showNameDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Language Menu
        DropdownMenu(
            expanded = showLanguageMenu,
            onDismissRequest = { showLanguageMenu = false }
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang) },
                    onClick = {
                        languageState = lang
                        onLanguageChange(lang)
                        showLanguageMenu = false
                    }
                )
            }
        }
    }
