package futur.apps.composeproject1.quizsystem.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.quizsystem.ui.theme.correctAnswerColor
import futur.apps.composeproject1.quizsystem.viewmodels.QuizUiState
import futur.apps.composeproject1.viewmodels.QuizEvent

/**
 * ================================================
 * QuizOptionsSection.kt
 *
 * This component displays all answer options for the current question.
 * - Supports both portrait and landscape layouts.
 * - Highlights selected, correct, and wrong answers.
 * - Uses QuizUiState to control selection and answer checking.
 *
 * Buyer Notes:
 * - Easily replace question.options with your own data source.
 * - Option cards automatically animate size changes on selection.
 * - Colors are customizable via theme or helper functions.
 * ================================================
 */
@Composable
fun QuizOptionsSection(
    isLandscape: Boolean,
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit,
) {
    if (isLandscape) {
        QuizOptionsLandscape(uiState, question, onEvent)
    } else {
        QuizOptionsPortrait(uiState, question, onEvent)
    }
}

/** Portrait layout for options */
@Composable
fun QuizOptionsPortrait(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    LazyColumn {
        items(question.options) { option ->
            QuizOptionItem(
                text = option,
                isSelected = uiState.selectedOptionText == option,
                isCorrect = uiState.isAnswerChecked && option == question.correctAnswer,
                isWrong = uiState.isAnswerChecked &&
                        uiState.selectedOptionText == option &&
                        option != question.correctAnswer
            ) {
                if (!uiState.isAnswerChecked) onEvent(QuizEvent.SelectOption(option))
            }
        }
    }
}

/** Landscape layout for options with spacing */
@Composable
fun QuizOptionsLandscape(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(question.options) { option ->
            QuizOptionItem(
                text = option,
                isSelected = uiState.selectedOptionText == option,
                isCorrect = uiState.isAnswerChecked && option == question.correctAnswer,
                isWrong = uiState.isAnswerChecked &&
                        uiState.selectedOptionText == option &&
                        option != question.correctAnswer
            ) {
                if (!uiState.isAnswerChecked) onEvent(QuizEvent.SelectOption(option))
            }
        }
    }
}

/**
 * Single option card
 *
 * Buyer Notes:
 * - Highlights selection, correct, or wrong answers automatically.
 * - Animates size changes for smooth UI feedback.
 */
@Composable
private fun QuizOptionItem(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    onClick: () -> Unit,
) {
    // Determine border and background colors based on state
    val (borderColor, backgroundColor) = when {
        isCorrect -> correctAnswerColor().copy(0.5f) to correctAnswerColor()
        isWrong -> MaterialTheme.colorScheme.error.copy(0.5f) to MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary.copy(0.5f) to MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(0.5f) to MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .animateContentSize(), // Smooth animation on selection
        onClick = onClick,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(backgroundColor)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected || isCorrect || isWrong) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun QuizOptionsPreview() {
    QuizOptionsLandscape(
        uiState = QuizUiState(),
        question = Question(
            questionText = "What is the capital of France?",
            options = listOf("Paris", "London", "Berlin", "Madrid"),
            correctAnswer = "Paris"
        ),
        onEvent = {}
    )
}
