package futur.apps.composeproject1.quizsystem.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import futur.apps.composeproject1.quizsystem.core.QuizEffectHandler
import futur.apps.composeproject1.quizsystem.ui.components.QuizActionButton
import futur.apps.composeproject1.quizsystem.ui.components.QuizHeaderSection
import futur.apps.composeproject1.quizsystem.ui.components.QuizOptionsSection
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.viewmodels.*

/**
 * ============================================================
 * 🎯 QuizScreen.kt
 *
 * Supports BuiltIn and UserCreated modes.
 * Handles:
 *  - StateFlow collection
 *  - Effect playback (sound / TTS)
 *  - Lifecycle-safe timer & sound handling
 *  - Portrait/Landscape layout variants
 * ============================================================
 */
@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onGoHome: (() -> Unit)? = null,
    onShareResult: (() -> Unit)? = null
) {
    val uiState by quizViewModel.ui.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    /* ------------------------------------------------------------
       🎬 Initialize quiz ONCE when category changes
       ------------------------------------------------------------ */
    LaunchedEffect(uiState.category) {
        val category = uiState.category
        if (category != null && uiState.questions.isEmpty() && !uiState.isLoading) {
            quizViewModel.initializeQuiz(category, quizViewModel.mode.value)
        }
    }

    /* ------------------------------------------------------------
       🎧 Play quiz effects (sounds / TTS)
       ------------------------------------------------------------ */
    QuizEffectHandler(
        quizViewModel = quizViewModel,
        effectsViewModel = effectsViewModel,
        settingsViewModel = settingsViewModel
    )

    Log.d("QuizScreen", "Current question index: ${uiState.currentIndex}")


    /* ------------------------------------------------------------
       🧩 UI Content
       ------------------------------------------------------------ */
    when {
        uiState.isLoading -> QuizLoadingScreen()

        uiState.isFinished -> QuizResultScreen(
            isLandscape = isLandscape,
            uiState = uiState,
            score = uiState.correctScore,
            total = uiState.totalQuestions,
            onRetry = {
                uiState.category?.let { quizViewModel.initializeQuiz(it, quizViewModel.mode.value) }
            },
            onHome = { onGoHome?.invoke() },
            onShare = { onShareResult?.invoke() }
        )

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
        category = QuizCategory.BuiltIn(BuildInCategory.Verbs)
    )
    QuizScreenPortrait(uiState = fakeUiState, question = fakeQuestion, onEvent = {})
}
