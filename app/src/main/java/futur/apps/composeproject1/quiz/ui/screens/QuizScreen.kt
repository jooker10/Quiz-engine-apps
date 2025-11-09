package futur.apps.composeproject1.quiz.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.quiz.core.AppConfig
import futur.apps.composeproject1.quiz.core.QuizEffectHandler
import futur.apps.composeproject1.quiz.ui.components.QuizActionButton
import futur.apps.composeproject1.quiz.ui.components.QuizHeaderSection
import futur.apps.composeproject1.quiz.ui.components.QuizOptionsSection
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.viewmodels.*

/**
 * ============================================================
 * 🎯 QuizScreen.kt
 *
 * Unified QuizScreen for both Default and Custom sources.
 * - Shows loading, finished, question, or empty state.
 * - Handles lifecycle-safe timers & effects.
 * ============================================================
 */
@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onGoHome: (() -> Unit)? = null,
    onShareResult: (() -> Unit)? = null
) {
    val uiState by quizViewModel.ui.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE



    LaunchedEffect(activity) {
        quizViewModel.attachHostActivity(activity)
    }

    LaunchedEffect(Unit) {
        if (!AppConfig.USE_FIRESTORE_SYNC) {
            Toast.makeText(
                context,
                "Offline Demo Mode: Firestore sync is disabled.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /* ------------------------------------------------------------
       🎬 Initialize quiz once when category changes
       ------------------------------------------------------------ */
    LaunchedEffect(Unit) {
        val currentCategory = uiState.category
        if (currentCategory is QuizCategory.Default &&
            AppConfig.USE_FIRESTORE_SYNC
        ) {
            val totalPoints = quizViewModel.getTotalPoints()
            authViewModel.updatePointsInFirestore(totalPoints)
        }
    }


    /* ------------------------------------------------------------
       🎧 Sound / TTS effect handler
       ------------------------------------------------------------ */
    QuizEffectHandler(
        quizViewModel = quizViewModel,
        effectsViewModel = effectsViewModel,
        settingsViewModel = settingsViewModel
    )

    Log.d("QuizScreen", "Current question index: ${uiState.currentIndex}")

    /* ------------------------------------------------------------
       🧩 UI Rendering Logic
       ------------------------------------------------------------ */
    when {
        uiState.isLoading -> QuizLoadingScreen()

        // 🚫 No questions available (especially for Custom)
        uiState.questions.isEmpty() && !uiState.isLoading -> NoQuestionsScreen()

        // ✅ Finished
        uiState.isFinished -> {
            // 🔹 Sync Firestore XP only for built-in mode
            LaunchedEffect(Unit) {
                val currentCategory = uiState.category
                if (currentCategory is QuizCategory.Default) {
                    val totalPoints = quizViewModel.getTotalPoints()
                    authViewModel.updatePointsInFirestore(totalPoints)
                }
            }

            QuizResultScreen(
                isLandscape = isLandscape,
                uiState = uiState,
                score = uiState.correctScore,
                total = uiState.totalQuestions,
                viewModel = quizViewModel, // ✅ newly added param
                onRetry = {
                    uiState.category?.let {
                        quizViewModel.initializeQuiz(it, quizViewModel.mode.value)
                    }
                },
                onHome = { onGoHome?.invoke() },
                onShare = { onShareResult?.invoke() }
            )

        }


        // ✅ Active question
        uiState.currentQuestion != null -> {
            val question = uiState.currentQuestion!!
            if (isLandscape) {
                QuizScreenLandscape(uiState = uiState, question = question, onEvent = quizViewModel::onEvent)
            } else {
                QuizScreenPortrait(uiState = uiState, question = question, onEvent = quizViewModel::onEvent)
            }
        }

        else -> QuizLoadingScreen()
    }

    /* ------------------------------------------------------------
       ⏸ Lifecycle observer — pause/resume timer & sounds
       ------------------------------------------------------------ */
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    effectsViewModel.stopTimerSounds()
                    quizViewModel.pauseTimer()
                }

                Lifecycle.Event.ON_RESUME -> quizViewModel.resumeTimer()
                Lifecycle.Event.ON_STOP -> effectsViewModel.stopTimerSounds()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

/* ============================================================
   📱 Portrait Layout
   ============================================================ */
@Composable
private fun QuizScreenPortrait(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        QuizHeaderSection(uiState = uiState, question = question)
        Spacer(modifier = Modifier.height(80.dp))
        QuizOptionsSection(
            isLandscape = false,
            uiState = uiState,
            question = question,
            onEvent = onEvent
        )
        Spacer(modifier = Modifier.weight(1f))
        QuizActionButton(uiState = uiState, onEvent = onEvent)
    }
}

/* ============================================================
   🖥 Landscape Layout
   ============================================================ */
@Composable
private fun QuizScreenLandscape(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            QuizHeaderSection(uiState = uiState, question = question)
            Spacer(modifier = Modifier.height(80.dp))
            QuizActionButton(uiState = uiState, onEvent = onEvent)
        }

        Spacer(modifier = Modifier.width(24.dp))

        QuizOptionsSection(
            isLandscape = true,
            uiState = uiState,
            question = question,
            onEvent = onEvent
        )
    }
}

/* ============================================================
   ⏳ Loading State
   ============================================================ */
@Composable
fun QuizLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

/* ============================================================
   🚫 No Questions State
   ============================================================ */
@Composable
fun NoQuestionsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No questions available in this category yet.",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        )
    }
}

/* ============================================================
   🧩 Preview
   ============================================================ */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    val fakeQuestion = Question(
        questionText = "What is the capital of France?",
        options = listOf("Paris", "London", "Berlin", "Madrid"),
        correctAnswer = "Paris"
    )
    val fakeUiState = QuizUiState(
        questions = listOf(fakeQuestion),
        currentIndex = 0,
        correctScore = 1,
        wrongScore = 0,
        timeLeft = 8,
        maxTime = 10,
        category = QuizCategory.Default(DefaultCategory.Verbs)
    )
    QuizScreenPortrait(uiState = fakeUiState, question = fakeQuestion, onEvent = {})
}
