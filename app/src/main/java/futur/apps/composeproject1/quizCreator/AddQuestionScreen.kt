package futur.apps.composeproject1.quizCreator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.utils.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    viewModel: QuizCreatorViewModel,
    selectedCategory: String,
    existingQuestion: Question? = null, // If null => Add, else => Edit
    onQuestionSaved: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val focusManager = LocalFocusManager.current

    var questionText by remember { mutableStateOf(existingQuestion?.questionText ?: "") }
    var options by remember { mutableStateOf(existingQuestion?.options ?: listOf("", "")) }
    var correctAnswer by remember { mutableStateOf(existingQuestion?.correctAnswer ?: "") }
    var category by remember { mutableStateOf(selectedCategory) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedAnswer by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (existingQuestion == null) "Add New Question" else "Edit Question",
            style = MaterialTheme.typography.headlineSmall
        )

        // Question Text
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question Text") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 3
        )

        // DefaultCategory Selector
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                label = { Text("Select DefaultCategory") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            category = cat.name
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        // Options
        Text("Options", style = MaterialTheme.typography.titleMedium)
        options.forEachIndexed { index, opt ->
            OutlinedTextField(
                value = opt,
                onValueChange = { newValue ->
                    options = options.toMutableList().also { it[index] = newValue }
                },
                label = { Text("Option ${index + 1}") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        TextButton(onClick = { options = options + "" }) {
            Text("➕ Add Option")
        }

        // Correct Answer Selector
        ExposedDropdownMenuBox(
            expanded = expandedAnswer,
            onExpandedChange = { expandedAnswer = !expandedAnswer }
        ) {
            OutlinedTextField(
                value = correctAnswer,
                onValueChange = {},
                label = { Text("Correct Answer") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAnswer)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedAnswer,
                onDismissRequest = { expandedAnswer = false }
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt.ifBlank { "(empty)" }) },
                        onClick = {
                            correctAnswer = opt
                            expandedAnswer = false
                        }
                    )
                }
            }
        }

        // Save Question Button
        Button(
            onClick = {
                focusManager.clearFocus()
                if (questionText.isNotBlank() && correctAnswer.isNotBlank() && options.size >= 2) {
                    val newQuestion = Question(questionText, options, correctAnswer)
                    if (existingQuestion == null) {
                        viewModel.addQuestion(category, newQuestion)
                    } else {
                        // Remove old question and add updated one
                        viewModel.removeQuestion(category, existingQuestion)
                        viewModel.addQuestion(category, newQuestion)
                    }
                    onQuestionSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (existingQuestion == null) "💾 Save Question" else "💾 Update Question")
        }
    }
}
