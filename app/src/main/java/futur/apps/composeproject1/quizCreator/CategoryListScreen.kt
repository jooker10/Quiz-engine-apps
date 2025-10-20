package futur.apps.composeproject1.quizCreator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.RoomDatabase.userroom.UserCategoryEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuestionEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel

@Composable
fun CategoryListScreen(
    viewModel: UserQuizViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryDescription by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // ---------- Header ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showAddCategoryDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add BuildInCategory")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No categories yet. Click + to add one.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // ---------- Add BuildInCategory Dialog ----------
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add BuildInCategory") },
            text = {
                Column {
                    TextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("BuildInCategory Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newCategoryDescription,
                        onValueChange = { newCategoryDescription = it },
                        placeholder = { Text("Description (optional)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        viewModel.addCategory(
                            name = newCategoryName,
                            description = newCategoryDescription.ifBlank { null }
                        )
                        newCategoryName = ""
                        newCategoryDescription = ""
                    }
                    showAddCategoryDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: UserCategoryEntity,
    viewModel: UserQuizViewModel
) {
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    val questions by viewModel.getQuestions(category.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // ---------- BuildInCategory Header ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (!category.description.isNullOrBlank()) {
                    Text(
                        text = category.description!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = { showAddQuestionDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Question")
                }
                IconButton(onClick = { viewModel.deleteCategory(category) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete BuildInCategory")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ---------- Questions List ----------
        if (questions.isEmpty()) {
            Text(
                text = "No questions yet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            questions.forEachIndexed { index, question ->
                QuestionCard(question = question, index = index) {
                    viewModel.deleteQuestion(question)
                }
            }
        }
    }

    // ---------- Add Question Dialog ----------
    if (showAddQuestionDialog) {
        AddQuestionDialog(
            onDismiss = { showAddQuestionDialog = false },
            onConfirm = { qText, opts, correct ->
                viewModel.addQuestion(category.id, qText, opts, correct)
                showAddQuestionDialog = false
            }
        )
    }
}

@Composable
fun QuestionCard(
    question: UserQuestionEntity,
    index: Int,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Q${index + 1}: ${question.questionText}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            question.options.forEachIndexed { optIndex, option ->
                val isCorrect = option == question.correctAnswer
                Text(
                    text = "${'A' + optIndex}. $option",
                    color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDelete) {
                Text("Delete Question", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>, String) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Question") },
        text = {
            Column {
                TextField(value = questionText, onValueChange = { questionText = it }, label = { Text("Question") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A") })
                TextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") })
                TextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Option C") })
                TextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Option D") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = correctAnswer, onValueChange = { correctAnswer = it }, label = { Text("Correct Answer") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (questionText.isNotBlank() && correctAnswer.isNotBlank()) {
                    val options = listOf(optionA, optionB, optionC, optionD).filter { it.isNotBlank() }
                    onConfirm(questionText, options, correctAnswer)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
