package futur.apps.composeproject1.quizsystem.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswersRow
import futur.apps.composeproject1.quizsystem.ui.theme.progressResultColor
import futur.apps.composeproject1.quizsystem.viewmodels.QuizUiState
import futur.apps.composeproject1.R


/**
 * ================================================
 * QuizResultScreen.kt
 *
 * 🔹 Main screen to display quiz results after completion.
 * 🔹 Supports both portrait and landscape layouts.
 * 🔹 Shows animated progress bars, percentage, stats, earned points.
 * 🔹 Review section displays correct/incorrect answers if enabled.
 * 🔹 Action buttons include Retry, Home, Share (customizable).
 *
 * Buyer Notes:
 * - Customize colors, icons, or actions via QuizConfig.
 * - Easily swap out ReviewAnswersRow or Stats layout for your design.
 * - Animations are smooth, spring-based, and ready for production.
 * - Perfect for Codester resale as a polished, ready-to-use results screen.
 * ================================================
 */
@Composable
fun QuizResultScreen(
    isLandscape: Boolean,
    uiState: QuizUiState,
    score: Int,
    total: Int,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    val percentage = (score.toFloat() / total.toFloat()) * 100f
    val progressColor = progressResultColor(percentage)

    // Animate entire screen appearance for smooth entry
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.8f, animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 0.8f, animationSpec = tween(400))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            if (!isLandscape) {
                // =========================
                // PORTRAIT LAYOUT
                // =========================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    ResultHeaderSection(score, total, progressColor, isLandscape)
                    ResultStatsSection(score, total, uiState.earnedPoints, progressColor)
                    ReviewSection(uiState)
                    ActionsSection(onRetry, onHome, onShare)
                }
            } else {
                // =========================
                // LANDSCAPE LAYOUT
                // =========================
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left column: progress + stats
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultHeaderSection(score, total, progressColor, isLandscape)
                        ResultStatsSection(score, total, uiState.earnedPoints, progressColor)
                    }

                    // Right column: review + actions
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        ReviewSection(uiState)
                        Spacer(modifier = Modifier.height(16.dp))
                        ActionsSection(onRetry, onHome, onShare)
                    }
                }
            }
        }
    }
}

// -------------------------- HEADER SECTION --------------------------
/**
 * ResultHeaderSection
 *
 * 🔹 Displays animated circular progress + percentage.
 * 🔹 Shows a result message based on percentage.
 * 🔹 Buyer can customize text, size, colors via MaterialTheme or QuizConfig.
 */
@Composable
fun ResultHeaderSection(score: Int, total: Int, progressColor: Color, isLandscape: Boolean) {
    val progressBarSize = if (isLandscape) 80.dp else 120.dp
    val spaceBetweenHeaderAndStats = if (isLandscape) 12.dp else 16.dp
    val strokeWith = if (isLandscape) 8.dp else 10.dp
    val textStyle = if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge

    var progressTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { progressTarget = score.toFloat() / total.toFloat() }

    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    val animatedPercentage by animateFloatAsState(
        targetValue = progressTarget * 100,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    val finalPercentage = remember(score, total) { (score.toFloat() / total.toFloat()) * 100 }

    var showMessage by remember { mutableStateOf(false) }
    LaunchedEffect(animatedProgress) {
        if (animatedProgress >= progressTarget) showMessage = true
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Circular progress indicator with animated percentage
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(progressBarSize),
                color = progressColor,
                strokeWidth = strokeWith,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            )
            Text(
                text = "${animatedPercentage.toInt()}%",
                style = textStyle.copy(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(spaceBetweenHeaderAndStats))

        // Display result message after animation
        AnimatedVisibility(
            visible = showMessage,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Text(
                text = stringResource(QuizConfig.getResultMessageRes(finalPercentage)),
                style = MaterialTheme.typography.titleMedium.copy(color = progressColor),
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------- STATS SECTION --------------------------
/**
 * ResultStatsSection
 *
 * 🔹 Displays quiz statistics like total questions, correct answers, and points.
 * 🔹 Each stat animates from 0 → target value.
 * 🔹 Buyer can swap icons or change layout easily.
 */
@Composable
fun ResultStatsSection(correctAnswers: Int, total: Int, points: Int, progressColor: Color) {
    val values = listOf(total, correctAnswers, points)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuizConfig.resultStatsConfig.forEachIndexed { index, config ->
            val value = values.getOrNull(index) ?: 0
            val color = if (config.useDynamicColor) progressColor else MaterialTheme.colorScheme.primary
            StatItem(icon = config.icon, label = stringResource(id = config.labelRes), targetValue = value, color = color)
        }
    }
}

/**
 * StatItem
 *
 * 🔹 Animates numeric value smoothly with overshoot effect.
 */
@Composable
fun StatItem(icon: ImageVector, label: String, targetValue: Int, color: Color) {
    val animatedValue = remember { Animatable(0f) }
    LaunchedEffect(targetValue) {
        animatedValue.animateTo(
            targetValue.toFloat(),
            animationSpec = tween(durationMillis = 1500, easing = { OvershootInterpolator(2f).getInterpolation(it) })
        )
    }
    StatItemWithIcon(icon, label, animatedValue.value.toInt().toString(), color)
}

@Composable
fun StatItemWithIcon(icon: ImageVector, label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(color = color))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

// -------------------------- REVIEW SECTION --------------------------
/**
 * ReviewSection
 *
 * 🔹 Optional section to review user answers.
 * 🔹 Displayed only if `uiState.enableReviewScreen` is true.
 */
@Composable
fun ReviewSection(uiState: QuizUiState) {
    if (uiState.enableReviewScreen) {
        Column {
            Text(
                text = stringResource(R.string.quiz_review_answers),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            ReviewAnswersRow(uiState.reviewAnswers)
        }
    }
}

// -------------------------- ACTION BUTTONS --------------------------
/**
 * ActionsSection
 *
 * 🔹 Displays Retry, Home, Share buttons.
 * 🔹 Button actions customizable via parameters.
 * 🔹 Icons and labels configurable via QuizConfig.
 */
@Composable
fun ActionsSection(onRetry: () -> Unit = {}, onHome: () -> Unit = {}, onShare: () -> Unit = {}) {
    val handlers = listOf(onRetry, onHome, onShare)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        QuizConfig.resultActionsConfig.forEachIndexed { index, action ->
            OutlinedButton(
                onClick = handlers.getOrNull(index) ?: {},
                modifier = Modifier.size(width = 90.dp, height = 48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = action.icon, contentDescription = stringResource(id = action.labelRes))
            }
        }
    }
}

// -------------------------- PREVIEW --------------------------
/**
 * PreviewQuizResultScreen
 *
 * 🔹 Helps test portrait layout in Android Studio preview.
 * 🔹 Create additional previews for landscape / dark mode if needed.
 */
@Preview(showBackground = true)
@Composable
fun PreviewQuizResultScreen() {
    val dummyUiState = QuizUiState(earnedPoints = 120, reviewAnswers = emptyList())
    QuizResultScreen(isLandscape = false, uiState = dummyUiState, score = 7, total = 10)
}
