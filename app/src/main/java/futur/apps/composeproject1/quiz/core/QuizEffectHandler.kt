package futur.apps.composeproject1.quiz.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiEffect
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.viewmodels.SettingsViewModel

/**
 * ============================================================
 * 🎧 QuizEffectHandler.kt
 *
 * Handles one-shot effects emitted by QuizViewModel.
 * Plays sounds, triggers TTS, or stops timer sounds.
 *
 * - Listens to [QuizViewModel.effect] flow
 * - Delegates audio/TTS logic to [EffectsViewModel]
 * ============================================================
 */
@Composable
fun QuizEffectHandler(
    quizViewModel: QuizViewModel,
    effectsViewModel: EffectsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settingsState by settingsViewModel.uiState.collectAsState()

    // 🔹 Initialize TTS once
    LaunchedEffect(Unit) {
        effectsViewModel.initTTS(context)
    }

    // 🔹 Collect ONE-SHOT effects from the ViewModel
    LaunchedEffect(Unit) {
        quizViewModel.effect.collectLatest { effect ->
            when (effect) {
                is QuizUiEffect.PlayCorrectSound ->
                    effectsViewModel.playCorrectSound(settingsState.soundEnabled, context)

                is QuizUiEffect.PlayWrongSound ->
                    effectsViewModel.playWrongSound(settingsState.soundEnabled, context)

                is QuizUiEffect.PlayTimerTick ->
                    effectsViewModel.playTimerTick(settingsState.soundEnabled, context)

                is QuizUiEffect.PlayTimerUrgent ->
                    effectsViewModel.playTimerUrgent(settingsState.soundEnabled, context)

                is QuizUiEffect.StopTimerSounds ->
                    effectsViewModel.stopTimerSounds()

                is QuizUiEffect.SpeakTextRes -> {
                    val text = context.getString(effect.resId)
                    effectsViewModel.speak(text, enableTTS = settingsState.ttsEnabled)
                }

                QuizUiEffect.None -> Unit
            }
        }
    }
}
