package futur.apps.composeproject1.quizCreator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.utils.Question

@Composable
fun AddQuestionScreen(
    viewModel: QuizCreatorViewModel,
    selectedCategory: String,
    onQuestionSaved: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(mutableListOf("", "")) }
    var correctAnswer by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(selectedCategory) }

    val categories by viewModel.categories.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            placeholder = { Text("Question Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown to select category
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text("Category: $category")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            category = cat.name
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Options", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(options.size) { index ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${index + 1}.", modifier = Modifier.width(24.dp))
                    TextField(
                        value = options[index],
                        onValueChange = { options[index] = it },
                        placeholder = { Text("Option ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { options.add("") }) { Text("Add Option") }

        Spacer(modifier = Modifier.height(8.dp))
        // Correct answer dropdown
        var expandedAnswer by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expandedAnswer = true }) {
                Text("Correct Answer: ${correctAnswer.ifBlank { "Select" }}")
            }
            DropdownMenu(expanded = expandedAnswer, onDismissRequest = { expandedAnswer = false }) {
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

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (questionText.isNotBlank() && correctAnswer.isNotBlank() && options.size >= 2) {
                viewModel.addQuestion(category, Question(questionText, options.toList(), correctAnswer))
                onQuestionSaved()
            }
        }) {
            Text("Save Question")
        }
    }
}
