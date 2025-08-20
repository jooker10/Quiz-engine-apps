package futur.apps.composeproject1.appScreens._Screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1._Mains.QuizViewModel
import futur.apps.composeproject1.utils.CategoryName

@Composable
fun QuizScreen(
    category: CategoryName?,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.quizUiState.collectAsState()

    // Load questions when category changes
    LaunchedEffect(category) { quizViewModel.setCategory(category) }

    // Listen to events (Correct/Wrong)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        quizViewModel.events.collect { event ->
            when (event) {
                QuizViewModel.EffectsEvent.CorrectAnswer -> Toast.makeText(
                    context,
                    "Correct!",
                    Toast.LENGTH_SHORT
                ).show()

                QuizViewModel.EffectsEvent.WrongAnswer -> Toast.makeText(
                    context,
                    "Wrong!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val question = uiState.questions.getOrNull(uiState.currentIndex) ?: return

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        // Score + Timer + Question Number
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score: ${uiState.score}")
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                CircularProgressIndicator(
                progress = { 1f - (uiState.timeLeft.toFloat() / (uiState.timeLimit.toFloat())) },
                    color = if (uiState.timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                Text("${uiState.timeLeft}s", textAlign = TextAlign.Center)
            }
            Text("${uiState.currentIndex + 1}/${uiState.questions.size}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = question.questionText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Options
        question.options.forEach { option ->
            QuizOption(
                text = option,
                isSelected = uiState.selectedOptionText == option,
                isCorrect = uiState.isAnswerChecked && option == question.correctAnswer,
                isWrong = uiState.isAnswerChecked && uiState.selectedOptionText == option && option != question.correctAnswer
            ) {
                quizViewModel.selectOption(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Confirm / Next / Finish
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
@Composable
fun QuizOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    onClick: () -> Unit
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
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
/*
@Composable
fun QuizScreen(
    category: CategoryName?,
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = hiltViewModel(),
) {
    val quizUiState by quizViewModel.quizUiState.collectAsState()
   */
/* val currentCategoryScore = quizUiState.category?.let {
        quizUiState.scoresByCategory[it] } ?: 0*//*
    // should be in HomeScreen for displaying Scores

    LaunchedEffect(category) {
        quizViewModel.setCategory(category)
    }
    LaunchedEffect(Unit) {
        quizViewModel.events.collect { event ->
            when (event) {
                QuizViewModel.EffectsEvent.CorrectAnswer -> {
                    effectsViewModel.playCorrectSound()
                    effectsViewModel.speak("CorrectAnswer Answer!")
                }

                QuizViewModel.EffectsEvent.WrongAnswer -> {
                    effectsViewModel.playWrongSound()
                    effectsViewModel.speak("WrongAnswer Answer!")
                }

            }
        }
    }
    if (quizUiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading questions....",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }

    } else if (quizUiState.questions.isNotEmpty()) {
        val question = quizUiState.questions[quizUiState.currentIndex]
        // val options = question.options

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: ${quizUiState.score}", style = MaterialTheme.typography.bodyMedium)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f - (quizUiState.timeLeft.toFloat() / 15f) },
                        modifier = Modifier.fillMaxSize(),
                        color = if (quizUiState.timeLeft < 5) Color.Red else MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                    )
                    Text(
                        text = "${quizUiState.timeLeft}s",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (quizUiState.timeLeft <= 5) Color.Red else Color.Unspecified,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    "${quizUiState.currentIndex + 1} / ${quizUiState.questions.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { index, optionText ->
                QuizOption(
                    text = optionText,
                    isSelected = quizUiState.selectedOptionText == optionText,
                    onClick = {
                        quizViewModel.selectOption(optionText)

                    }
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    quizViewModel.confirmOrNext()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = (quizUiState.selectedOptionText != null || quizUiState.isAnswerChecked)
            ) {
                Text(
                    text =
                        when {
                            !quizUiState.isAnswerChecked -> "Confirm"
                            quizUiState.currentIndex == quizUiState.questions.size - 1 -> "Finish"
                            else -> "Next"
                        }
                )

            }
        }
    }
}

@Composable
fun QuizOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) Color(0xFF4CAF50) else Color.Gray
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        BorderStroke(2.dp, if (isSelected) Color(0xFF4CAF50) else Color.Gray),
                        shape = CircleShape
                    )
                    .background(
                        if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

*/
