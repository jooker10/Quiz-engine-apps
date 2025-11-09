package futur.apps.composeproject1.quizCreator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import futur.apps.composeproject1.RoomDatabase.userroom.*
import futur.apps.composeproject1.userquiz.AddQuestionScreen
import futur.apps.composeproject1.quiz.ui.theme.correctAnswerColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryQuestionsScreen(
    categoryId: Int,
    navController: NavController,
    viewModel: UserQuizViewModel = hiltViewModel()
) {
    val questions by viewModel.getQuestions(categoryId).collectAsState(initial = emptyList())
    val category by viewModel.getCategoryById(categoryId).collectAsState(initial = null)
    val categoryTitle = category?.name ?: "Questions"

    var showAdd by remember { mutableStateOf(false) }
    var editQuestion by remember { mutableStateOf<UserQuestionEntity?>(null) }
    var questionToDelete by remember { mutableStateOf<UserQuestionEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        categoryTitle,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Question", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (questions.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No questions yet.\nTap the + button to add one.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(questions, key = { it.id }) { q ->
                    QuestionCardModern(
                        question = q,
                        onEdit = { editQuestion = q },
                        onDelete = { questionToDelete = q }
                    )
                }
            }
        }
    }

    if (showAdd) {
        AddQuestionScreen(
            categoryId = categoryId,
            onDismiss = { showAdd = false },
            onQuestionSaved = { showAdd = false }
        )
    }

    editQuestion?.let { q ->
        AddQuestionScreen(
            categoryId = categoryId,
            existingQuestion = q,
            onDismiss = { editQuestion = null },
            onQuestionSaved = { editQuestion = null }
        )
    }

    questionToDelete?.let { q ->
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Delete Question") },
            text = { Text("Are you sure you want to delete this question?\n\n${q.questionText}") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteQuestion(q)
                    questionToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

/* ---------------------------------------------------------------
   ✨ Modern Question Card with Expandable Options
---------------------------------------------------------------- */
@Composable
private fun QuestionCardModern(
    question: UserQuestionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = question.questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            AnimatedVisibility(expanded) {
                Column(modifier = Modifier.padding(start = 48.dp, top = 6.dp, end = 8.dp)) {
                    question.options.forEachIndexed { i, option ->
                        val isCorrect = option == question.correctAnswer
                        OptionCardPreview(
                            text = option,
                            isCorrect = isCorrect
                        )
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------------------
   ✅ Read-only Option Card (Styled like QuizOptionItem)
---------------------------------------------------------------- */
@Composable
private fun OptionCardPreview(
    text: String,
    isCorrect: Boolean
) {
    val backgroundColor = if (isCorrect)
        correctAnswerColor()      // solid color for correct answer
    else
        MaterialTheme.colorScheme.surface

    val textColor = if (isCorrect)
        Color.White               // white text for green background
    else
        MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
