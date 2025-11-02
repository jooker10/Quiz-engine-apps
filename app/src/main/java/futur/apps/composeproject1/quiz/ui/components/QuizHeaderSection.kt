package futur.apps.composeproject1.quiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.quiz.core.AppConfig
import futur.apps.composeproject1.quiz.ui.theme.correctAnswerColor
import futur.apps.composeproject1.quiz.ui.theme.wrongAnswerColor
import futur.apps.composeproject1.viewmodels.QuizUiState
import futur.apps.composeproject1.R

/**
 * ================================================
 * QuizHeaderSection.kt
 *
 * 🔹 Displays the top section of a quiz screen.
 * 🔹 Includes:
 *    - Correct/wrong score indicators (progress bars)
 *    - Question counter (current / total)
 *    - Question text
 *    - Timer (circular, with critical threshold color)
 *
 * Buyer Notes:
 * - Score indicators are dynamic and animated via QuizUiState.
 * - Customize colors using theme overrides (correctAnswerColor / wrongAnswerColor).
 * - Timer color automatically changes below critical threshold (TIMER_CRITICAL_THRESHOLD).
 * - Layout keeps consistent spacing when certain elements are hidden.
 * - Reusable in multiple quiz apps; just pass uiState and Question.
 * ================================================
 */
@Composable
fun QuizHeaderSection(
    uiState: QuizUiState,
    question: Question,
) {
    val questionCounterSize = 14.sp
    val questionTextSize = 16.sp

    Box(contentAlignment = Alignment.TopCenter) {

        // --- Main Card Container ---
        Card(
            modifier = Modifier.padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // =========================
                // SCORE INDICATORS
                // - Displays correct and optional wrong answer progress
                // =========================
                if (uiState.showProgressBar) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoreIndicator(
                            score = uiState.correctScore,
                            color = correctAnswerColor(),
                            progress = uiState.correctScore.toFloat() / uiState.totalQuestions
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (uiState.enableNegativeScoring) {
                            ScoreIndicator(
                                score = uiState.wrongScore,
                                color = wrongAnswerColor(),
                                progress = uiState.wrongScore.toFloat() / uiState.totalQuestions
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    // Reserve space to maintain consistent layout
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // =========================
                // QUESTION INDEX
                // - Example: "Q1 / 10"
                // =========================
                if (uiState.showQuestionIndex) {
                    Text(
                        text = stringResource(
                            R.string.quiz_question_count,
                            uiState.currentIndex + 1,
                            uiState.totalQuestions
                        ),
                        fontSize = questionCounterSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // =========================
                // QUESTION TEXT
                // =========================
                Text(
                    text = question.questionText,
                    fontSize = questionTextSize,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // =========================
        // TIMER INDICATOR
        // - Circular progress timer
        // - Changes color below critical threshold
        // - Maintains layout consistency when hidden
        // =========================
        if (uiState.showTimer) {
            TimerIndicator(
                timeLeft = uiState.timeLeft,
                maxTime = uiState.maxTime
            )
        } else {
            Spacer(modifier = Modifier.height(35.dp)) // Placeholder for layout alignment
        }
    }
}

/**
 * ScoreIndicator
 *
 * 🔹 Shows numeric score + progress bar.
 * 🔹 Color matches correct/wrong answers theme.
 * 🔹 Progress value should be between 0f and 1f.
 */
@Composable
private fun ScoreIndicator(score: Int, color: Color, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$score",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(4.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(40.dp)
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.3f),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

/**
 * TimerIndicator
 *
 * 🔹 Circular countdown timer.
 * 🔹 Color changes if timeLeft <= critical threshold.
 * 🔹 Size and stroke width customizable via constants.
 */
@Composable
private fun TimerIndicator(timeLeft: Int, maxTime: Int) {
    val size = 70.dp
    val strokeWidth = 7.dp
    val textSize = 20.sp

    Surface(
        modifier = Modifier.offset(y = (-size / 2)),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(contentAlignment = Alignment.Center) {
            val criticalTime = minOf(AppConfig.TIMER_CRITICAL_THRESHOLD, maxTime)
            CircularProgressIndicator(
                progress = { 1f - (timeLeft.toFloat() / maxTime.toFloat()) },
                modifier = Modifier.size(size),
                color = if (timeLeft <= criticalTime) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                strokeWidth = strokeWidth,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            )
            Text(
                text = "$timeLeft",
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                color = if (timeLeft <= criticalTime) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Preview for buyers
 *
 * 🔹 Demonstrates layout with sample question, scores, and timer.
 */
@Preview(showBackground = true)
@Composable
fun HeaderSectionPreview() {
    QuizHeaderSection(
        uiState = QuizUiState(
            questions = listOf(
                Question(
                    questionText = "What is the capital of France?",
                    options = listOf("Paris", "London", "Berlin", "Madrid"),
                    correctAnswer = "Paris"
                )
            ),
            currentIndex = 0,
            correctScore = 1,
            wrongScore = 0,
            timeLeft = 8,
            maxTime = 10
        ),
        question = Question(
            questionText = "What is the capital of France?",
            options = listOf("Paris", "London", "Berlin", "Madrid"),
            correctAnswer = "Paris"
        )
    )
}
