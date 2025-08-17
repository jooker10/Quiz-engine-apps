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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.utils.CategoryName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerScreen(
    initialSelection: String? = null,
    onCategorySelected: (CategoryName) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(initialSelection ?: "") }
    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Choose a Category",
            style = MaterialTheme.typography.titleLarge
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = !expanded}
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selected.ifEmpty { "Select..." },
                onValueChange = {},
                label = {Text(text = "Category")},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded)}
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                CategoryName.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {Text(text = option.displayName)},
                        onClick = {
                            selected = option.displayName
                            expanded = false
                            focusManager.clearFocus()
                            onCategorySelected(option)
                        }
                    )

                }
            }
        }

    }


}