package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.utils.Category

/**
 * PickerScreen
 *
 * A reusable screen that allows users to select a quiz category from a dropdown list.
 *
 * @param initialSelection (Optional) Pre-selected category when the screen loads.
 * @param onCategorySelected Callback function triggered when the user selects a category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerScreen(
    initialSelection: Category? = null,
    onCategorySelected: (Category) -> Unit = {}
) {
    // Controls whether the dropdown is expanded
    var isExpanded by remember { mutableStateOf(false) }

    // Stores the currently selected category (persisted across config changes)
    var selectedCategory by rememberSaveable { mutableStateOf(initialSelection) }

    // Used to clear focus after selection (prevents keyboard from popping up)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Choose a Category",
            style = MaterialTheme.typography.titleLarge
        )

        // Exposed Dropdown Menu Box
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            // Read-only input field showing the selected category
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedCategory?.displayName ?: "Select...",
                onValueChange = {},
                label = { Text(text = "Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) }
            )

            // Dropdown menu content
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                Category.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.displayName) },
                        onClick = {
                            selectedCategory = option
                            isExpanded = false
                            focusManager.clearFocus()
                            onCategorySelected(option) // Trigger callback
                        }
                    )
                }
            }
        }

        // Show AssistChip with the currently selected category
        selectedCategory?.let { chosen ->
            AssistChip(
                onClick = { isExpanded = true },
                label = { Text(text = "Selected: ${chosen.displayName}") }
            )
            }
     }
}

