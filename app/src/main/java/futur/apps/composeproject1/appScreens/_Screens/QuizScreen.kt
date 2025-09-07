package futur.apps.composeproject1.appScreens._Screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.ResultDialogQuiz
import futur.apps.composeproject1.ui.theme.lightGreen
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiState
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
    effectsViewModel: EffectsViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.quizUiState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

    // Load questions when category changes
    LaunchedEffect(category) {
        quizViewModel.setCategory(activity = activity, category = category)
    }

    LaunchedEffect(Unit) {
        quizViewModel.confirmOrNextEvent.collect {
            quizViewModel.confirmOrNext(activity)
        }
    }

    // Listen to quiz events (Correct/Wrong)
    LaunchedEffect(Unit) {
        quizViewModel.events.collect { event ->
            when (event) {
                QuizViewModel.QuizEffect.CorrectAnswer -> {
                    Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                    effectsViewModel.playCorrectSound()
                    effectsViewModel.speak("Correct answer!")
                }

                QuizViewModel.QuizEffect.WrongAnswer -> {
                    Toast.makeText(context, "Wrong!", Toast.LENGTH_SHORT).show()
                    effectsViewModel.playWrongSound()
                        effectsViewModel.speak("Wrong answer!")

                }

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
        ResultDialogQuiz(
            completion = if (uiState.questions.isNotEmpty()) (uiState.correctScore * 100 / uiState.questions.size) else 0,
            totalQuestions = uiState.questions.size,
            correctAnswers = uiState.correctScore,
            wrongAnswers = uiState.questions.size - uiState.correctScore
        )
        return
    }

    val currentQuestion = uiState.questions.getOrNull(uiState.currentIndex) ?: return

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row: Score, Timer, Question number
        Box(contentAlignment = Alignment.Center) {

            QuizHeader()
            HeaderQuestionSection(uiState = uiState, currentQuestion = currentQuestion)
        }

        Spacer(modifier = Modifier.height(80.dp))


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

        ConfirmNextButton(
            quizViewModel = quizViewModel,
            uiState = uiState,
            activity = activity
        )
    }

    }



@Composable
fun ConfirmNextButton(
    quizViewModel : QuizViewModel = hiltViewModel(),
    uiState : QuizUiState,
    activity: Activity
) {
    // Confirm / Next / Finish Button
    Button(
        onClick = { quizViewModel.confirmOrNext(activity = activity) },
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


/**
 * Top Bar for Quiz Screen showing Score, Timer, and Question number.
 */

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

@Composable
fun QuizHeader() {
    // Implementation of the top bar
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(lightGreen.copy(alpha = 0.5f), lightGreen)
                )
            )
    )
}
