package futur.apps.composeproject1.appScreens._Screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.appScreens.ResultSheet
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.viewmodels.QuizViewModel

/**
 * Main Quiz Screen composable that handles the full quiz UI.
 * @param category The selected category of quiz questions
 * @param quizViewModel The ViewModel managing quiz state and logic
 */
@Composable
fun QuizScreen(
    category: Category?,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.quizUiState.collectAsState()

    // Load questions when category changes
    LaunchedEffect(category) {
        quizViewModel.setCategory(category)
    }

    val context = LocalContext.current
    // Listen to quiz events (Correct/Wrong)
    LaunchedEffect(Unit) {
        quizViewModel.events.collect { event ->
            when (event) {
                QuizViewModel.QuizEffect.CorrectAnswer ->
                    Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                QuizViewModel.QuizEffect.WrongAnswer ->
                    Toast.makeText(context, "Wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Loading State
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Display Result Sheet if quiz is finished
    if (uiState.isFinished) {
        ResultSheet(
            quizUiState = uiState,
            onRetry = { quizViewModel.retryQuiz() }
        )
        return
    }

    val currentQuestion = uiState.questions.getOrNull(uiState.currentIndex) ?: return

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Top Row: Score, Timer, Question number
        QuizTopBar(uiState)

        Spacer(modifier = Modifier.height(24.dp))

        // Display the current question
        Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quiz options list
        currentQuestion.options.forEach { option ->
            QuizOption(
                text = option,
                isSelected = uiState.selectedOptionText == option,
                isCorrect = uiState.isAnswerChecked && option == currentQuestion.correctAnswer,
                isWrong = uiState.isAnswerChecked &&
                        uiState.selectedOptionText == option &&
                        option != currentQuestion.correctAnswer
            ) { quizViewModel.selectOption(option) }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Confirm / Next / Finish Button
        Button(
            onClick = { quizViewModel.confirmOrNext() },
            enabled = (uiState.selectedOptionText != null || uiState.isAnswerChecked),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when {
                    !uiState.isAnswerChecked -> "Confirm"
                    uiState.currentIndex == uiState.questions.size - 1 -> "Finish"
                    else -> "Next"
                }
            )
        }
    }
}

/**
 * Top Bar for Quiz Screen showing Score, Timer, and Question number.
 */
@Composable
fun QuizTopBar(uiState: futur.apps.composeproject1.viewmodels.QuizUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Current Score
        Text(text = "Score: ${uiState.score}")

        // Timer Indicator
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
            CircularProgressIndicator(
                progress = 1f - (uiState.timeLeft.toFloat() / uiState.timeLimit.toFloat()),
                color = if (uiState.timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            )
            Text("${uiState.timeLeft}s", textAlign = TextAlign.Center)
        }

        // Current Question Index
        Text("${uiState.currentIndex + 1}/${uiState.questions.size}")
    }
}

/**
 * Single Quiz Option composable with selection and correctness feedback.
 * @param text The option text
 * @param isSelected Whether this option is currently selected
 * @param isCorrect Whether this option is the correct answer
 * @param isWrong Whether this option was selected incorrectly
 * @param onClick Callback when option is clicked
 */
@Composable
fun QuizOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    onClick: () -> Unit,
) {
    val borderColor = when {
        isCorrect -> Color(0xFF4CAF50)
        isWrong -> Color.Red
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Gray
    }

    val backgroundColor = when {
        isCorrect -> Color(0xFFE8F5E9)
        isWrong -> Color(0xFFFFEBEE)
        isSelected -> Color(0xFFE3F2FD)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() }
            .animateContentSize(),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(BorderStroke(2.dp, borderColor), CircleShape)
                    .background(if (isSelected) borderColor else Color.Transparent, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
            }
        }
}