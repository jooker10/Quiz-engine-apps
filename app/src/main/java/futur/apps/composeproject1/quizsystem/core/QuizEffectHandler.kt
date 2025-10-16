package futur.apps.composeproject1.quizsystem.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiEffect
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.viewmodels.SettingsViewModel

@Composable
fun QuizEffectHandler(
    quizViewModel: QuizViewModel,
    effectsViewModel: EffectsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current

    // Collect latest settings state
    val settingsState by settingsViewModel.uiState.collectAsState()

    // 🔹 Initialize TTS once
    LaunchedEffect(Unit) {
        effectsViewModel.initTTS(context)
    }

    // 🔹 Collect ONE-TIME effects (sounds, TTS, etc.)
    LaunchedEffect(Unit) {
        quizViewModel.quizUiEffect.collectLatest { effect ->
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


/*
package futur.apps.composeproject1.quizsystem.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiEffect
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.viewmodels.SettingsViewModel

@Composable
fun QuizEffectHandler(
    quizViewModel: QuizViewModel,
    effectsViewModel: EffectsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current

    // 🔹 Initialize TTS once
    LaunchedEffect(Unit) {
        effectsViewModel.initTTS(context)
    }

    // 🔹 Collect ONE-TIME effects (sounds, TTS, etc.)
    LaunchedEffect(Unit) {
        quizViewModel.quizUiEffect.collectLatest { effect ->
           // val uiState = quizViewModel.quizUiState.value
            val settingsState = settingsViewModel.uiState.value


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
*/
