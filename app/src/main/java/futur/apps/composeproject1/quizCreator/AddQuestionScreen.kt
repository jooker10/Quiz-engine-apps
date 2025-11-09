package futur.apps.composeproject1.userquiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuestionEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    viewModel: UserQuizViewModel = hiltViewModel(),
    categoryId: Int,
    existingQuestion: UserQuestionEntity? = null,
    onDismiss: () -> Unit,
    onQuestionSaved: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var questionText by remember { mutableStateOf(existingQuestion?.questionText ?: "") }
    var options by remember { mutableStateOf(existingQuestion?.options ?: listOf("", "")) }
    var correctAnswer by remember { mutableStateOf(existingQuestion?.correctAnswer ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (existingQuestion == null) "Add Question" else "Edit Question",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // 🔹 Question text
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // 🔹 Options
                repeat(options.size) { index ->
                    OutlinedTextField(
                        value = options[index],
                        onValueChange = { newValue ->
                            options = options.toMutableList().also { it[index] = newValue }
                            // If this option was the correct answer, update it
                            if (correctAnswer == options[index]) correctAnswer = newValue
                        },
                        label = { Text("Option ${'A' + index}") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 🔹 Add new option
                TextButton(onClick = { options = options + "" }) {
                    Text("➕ Add Option")
                }

                // 🔹 Correct answer dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = correctAnswer,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Correct Answer") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        options.filter { it.isNotBlank() }.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    correctAnswer = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (questionText.isNotBlank() &&
                        correctAnswer.isNotBlank() &&
                        options.any { it == correctAnswer }) {
                        focusManager.clearFocus()
                        if (existingQuestion != null) viewModel.deleteQuestion(existingQuestion)
                        viewModel.addQuestion(categoryId, questionText, options, correctAnswer)
                        onQuestionSaved()
                    }
                }
            ) {
                Text(if (existingQuestion == null) "Save" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
