package futur.apps.composeproject1.quizCreator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CategoryListScreen(
    viewModel: QuizCreatorViewModel = hiltViewModel(),
    onAddQuestionClick: (categoryName: String) -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Category: ${category.name}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        category.questions.forEachIndexed { index, q ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("${index + 1}. ${q.questionText}")
                                    Text("Options: ${q.options.joinToString()}")
                                    Text("Answer: ${q.correctAnswer}")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { onAddQuestionClick(category.name) }) {
                                Text("Add Question")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { viewModel.removeCategory(category) }) {
                                Text("Delete Category")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var newCategory by remember { mutableStateOf("") }
            TextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                placeholder = { Text("New Category") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (newCategory.isNotBlank()) {
                    viewModel.addCategory(newCategory)
                    newCategory = ""
                }
            }) {
                Text("Add Category")
            }
        }
    }
}
