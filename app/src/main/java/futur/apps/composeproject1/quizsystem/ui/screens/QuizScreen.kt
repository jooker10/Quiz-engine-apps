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
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.quizsystem.core.QuizEffectHandler
import futur.apps.composeproject1.quizsystem.ui.components.QuizActionButton
import futur.apps.composeproject1.quizsystem.ui.components.QuizHeaderSection
import futur.apps.composeproject1.quizsystem.ui.components.QuizOptionsSection
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiState
import futur.apps.composeproject1.viewmodels.QuizEvent
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.viewmodels.SettingsViewModel

@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by quizViewModel.quizUiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Initialize quiz when Activity becomes available.
    // We call `setCategory(activity, category)` which is present in your original viewmodel
    // and internally triggers the appropriate start function (startNewQuiz / startQuiz).
    LaunchedEffect(Unit) {
        quizViewModel.setCategory(
            activity = activity,
            category = uiState.category
        )
    }


    /*  LaunchedEffect(activity) {
          activity?.let { act ->
              // If a category already exists in uiState we keep it; otherwise default to Verbs
              val categoryToUse = uiState.category ?: Category.Verbs
             // quizViewModel.setCategory(act, categoryToUse)
          }
      }*/

    // Effect handler (TTS, sounds)
    QuizEffectHandler(
        quizViewModel = quizViewModel,
        effectsViewModel = effectsViewModel,
        settingsViewModel = settingsViewModel
    )

    // Debug log (optional)
    Log.d("QuizMainScreen", "uiState.currentQuestion = ${uiState.currentQuestion}")

    when {
        // Loading state (show loader while questions are loading)
        uiState.isLoading -> QuizLoadingScreen()

        // Finished → show result screen
        uiState.isFinished -> QuizResultScreen(
            isLandscape = isLandscape,
            uiState = uiState,
            score = uiState.correctScore,
            total = uiState.totalQuestions,
            // Retry should restart quiz with same category; we pass activity because your original
            // start flow requires it.
            onRetry = {
               /* activity?.let { act ->
                    quizViewModel.setCategory(act, uiState.category ?: Category.Verbs)
                }*/
            },
            onHome = { /* TODO: implement navigation to Home */ },
            onShare = { /* TODO: implement share functionality */ }
        )

        // Active quiz → show question UI if available
        uiState.currentQuestion != null -> {
            val question = uiState.currentQuestion!!
            if (isLandscape) {
                QuizScreenLandscape(
                    uiState = uiState,
                    question = question,
                    onEvent = quizViewModel::onEvent
                )
            } else {
                QuizScreenPortrait(
                    uiState = uiState,
                    question = question,
                    onEvent = quizViewModel::onEvent
                )
            }
        }

        // Fallback: still loading / no question - show loader
        else -> QuizLoadingScreen()
    }

    // Observe lifecycle events to pause/resume timer & stop sounds
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    effectsViewModel.stopTimerSounds()
                    quizViewModel.pauseTimer()
                }
                Lifecycle.Event.ON_RESUME -> {
                    quizViewModel.resumeTimer()
                }
                Lifecycle.Event.ON_STOP -> {
                    effectsViewModel.stopTimerSounds()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun QuizScreenPortrait(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
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

@Composable
private fun QuizScreenLandscape(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
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

@Composable
fun QuizLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenLandscapePreview() {
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
        maxTime = 10
    )

    QuizScreenLandscape(uiState = fakeUiState, question = fakeQuestion, onEvent = {})
}

/*
package futur.apps.composeproject1.quizsystem.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.quizsystem.core.QuizEffectHandler
import futur.apps.composeproject1.quizsystem.ui.components.QuizActionButton
import futur.apps.composeproject1.quizsystem.ui.components.QuizHeaderSection
import futur.apps.composeproject1.quizsystem.ui.components.QuizOptionsSection
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.quizsystem.viewmodels.QuizEvent
import futur.apps.composeproject1.viewmodels.QuizUiState
import futur.apps.composeproject1.quizsystem.viewmodels.QuizViewModel

*/
/**
 * ================================================
 * Main Quiz Screen
 *
 * 🔹 Codester Buyers Notes:
 * - This is the central screen for displaying quizzes.
 * - Supports portrait & landscape layouts.
 * - Handles loading, active quiz, and result states.
 * - Integrates TTS and sound effects via QuizEffectHandler.
 * - Easily extendable for navigation, ads, or analytics.
 *
 * @param quizViewModel Main ViewModel holding quiz state and logic
 * @param effectsViewModel Handles sound effects & TTS
 * ================================================
 *//*

@Composable
fun QuizMainScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = viewModel(),
) {
    // Collect current UI state from ViewModel
    val uiState by quizViewModel.uiState.collectAsState()

    // Context & configuration access for orientation & resources
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val activity = LocalContext.current as? Activity

    LaunchedEffect(activity) {
        activity?.let {
            quizViewModel.initQuiz(it)
        }
    }

    // Handle TTS and sound effects via centralized handler
    QuizEffectHandler(
        quizViewModel = quizViewModel,
        effectsViewModel = effectsViewModel
    )

    // ===============================
    // Decide which screen to render
    // ===============================
    Log.e("eroor", "QuizMainScreen: ${uiState.currentQuestion}")
    when {
        // Loading state
        uiState.isLoading -> QuizLoadingScreen()

        // Quiz finished → show result screen
        uiState.isFinished -> QuizResultScreen(
            isLandscape = isLandscape,
            uiState = uiState,
            score = uiState.correctScore,
            total = uiState.totalQuestions,
            onRetry = { quizViewModel.startQuiz(uiState.category!!) }, // restart quiz on retry
            onHome = { */
/* TODO: implement navigation to Home *//*
 },
            onShare = { */
/* TODO: implement share functionality *//*
 }
        )

        // Active quiz → show question based on orientation


        else ->
            uiState.currentQuestion?.let { question ->
            if (isLandscape) {
                QuizScreenLandscape(
                    uiState = uiState,
                    question = question,
                    onEvent = quizViewModel::onEvent
                )
            } else {
                QuizScreenPortrait(
                    uiState = uiState,
                    question = question,
                    onEvent = quizViewModel::onEvent
                )
            }
        }
    }

    // Observe lifecycle events to handle timer & sounds
    val lifecycleOwner = LocalLifecycleOwner.current
    LifeCycleObserverApp(
        lifecycleOwner = lifecycleOwner,
        quizViewModel = quizViewModel,
        effectsViewModel = effectsViewModel
    )
}

*/
/**
 * ================================================
 * Lifecycle Observer
 *
 * 🔹 Codester Buyers Notes:
 * - Handles pause/resume/stop/destroy events safely.
 * - Pauses timer on background, stops sounds to prevent leaks.
 * - Do NOT manually call ViewModel.onCleared() here in production.
 * ================================================
 *//*

@Composable
fun LifeCycleObserverApp(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    quizViewModel: QuizViewModel,
    effectsViewModel: EffectsViewModel
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    effectsViewModel.stopTimerSounds()
                    quizViewModel.pauseTimer()
                }
                Lifecycle.Event.ON_RESUME -> {
                    quizViewModel.resumeTimer()
                }
                Lifecycle.Event.ON_STOP -> {
                    effectsViewModel.stopTimerSounds()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    // Timer & sounds will be cleared automatically by ViewModel
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

*/
/**
 * ================================================
 * Portrait Layout
 *
 * 🔹 Codester Buyers Notes:
 * - Vertical layout with header, options, and action button.
 * - Spacer & padding ensure consistent design across devices.
 * - Easily replace QuizHeaderSection or QuizOptionsSection for custom UI.
 * ================================================
 *//*

@Composable
private fun QuizScreenPortrait(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Question header: text + timer + progress
        QuizHeaderSection(uiState = uiState, question = question)

        Spacer(modifier = Modifier.height(80.dp))

        // Options buttons
        QuizOptionsSection(
            isLandscape = false,
            uiState = uiState,
            question = question,
            onEvent = onEvent
        )

        Spacer(modifier = Modifier.weight(1f))

        // Confirm / Next button
        QuizActionButton(uiState = uiState, onEvent = onEvent)
    }
}

*/
/**
 * ================================================
 * Landscape Layout
 *
 * 🔹 Codester Buyers Notes:
 * - Horizontal split: Left = header+button, Right = options.
 * - Ideal for tablets or large screens.
 * - Adjust `fillMaxWidth(0.5f)` to change column ratio.
 * ================================================
 *//*

@Composable
private fun QuizScreenLandscape(
    uiState: QuizUiState,
    question: Question,
    onEvent: (QuizEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Left column: header + confirm button
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

        // Right column: options buttons
        QuizOptionsSection(
            isLandscape = true,
            uiState = uiState,
            question = question,
            onEvent = onEvent
        )
    }
}

*/
/**
 * ================================================
 * Loading Screen
 *
 * 🔹 Codester Buyers Notes:
 * - Shows CircularProgressIndicator while loading questions.
 * - Theme-aware color for light/dark mode.
 * ================================================
 *//*

@Composable
fun QuizLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

*/
/**
 * ================================================
 * Preview: Landscape Layout
 *
 * 🔹 Codester Buyers Notes:
 * - Useful for layout testing in Android Studio Preview.
 * - Create additional previews for portrait / dark mode if desired.
 * ================================================
 *//*

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1280dp,height=720dp,orientation=landscape"
)
@Composable
fun QuizScreenLandscapePreview() {
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
        maxTime = 10
    )

    QuizScreenLandscape(uiState = fakeUiState, question = fakeQuestion, onEvent = {})
}

*/
