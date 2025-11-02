package futur.apps.composeproject1.quiz.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.quiz.ui.theme.correctAnswerColor
import futur.apps.composeproject1.quiz.ui.theme.wrongAnswerColor

/**
 * ================================================
 * ReviewAnswersRow.kt
 *
 * Components to display review of quiz answers:
 * - ReviewAnswerCard: Shows individual question review
 *   - Highlights correct and incorrect answers
 *   - Shows user answer and correct answer
 * - ReviewAnswersRow: Horizontally scrollable row of ReviewAnswerCards
 *
 * Buyer Notes:
 * - Customize card size, colors, padding, and icons.
 * - Can be integrated with QuizResultScreen for review section.
 * - Supports both short and long answers (ellipsis for overflow).
 * ================================================
 */

data class ReviewAnswer(
    val index: Int,          // Question index (0-based)
    val userAnswer: String,  // Answer selected by the user
    val correctAnswer: String // Correct answer
)

@Composable
fun ReviewAnswerCard(answer: ReviewAnswer) {
    val isCorrect = answer.userAnswer.trim().equals(answer.correctAnswer.trim(), ignoreCase = true)
    val answerColor = if (isCorrect) correctAnswerColor() else wrongAnswerColor()
    val cardBackground = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(140.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ---------- Question Index ----------
            Text(
                text = "Q${answer.index + 1}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )

            // ---------- User Answer ----------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, answerColor, RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = answerColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = answer.userAnswer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = answerColor,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ---------- Correct Answer ----------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, correctAnswerColor(), RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = correctAnswerColor(),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = answer.correctAnswer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = correctAnswerColor(),
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Horizontally scrollable row to display multiple ReviewAnswerCards
 */
@Composable
fun ReviewAnswersRow(answers: List<ReviewAnswer>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(answers) { answer ->
            ReviewAnswerCard(answer = answer)
        }
    }
}
