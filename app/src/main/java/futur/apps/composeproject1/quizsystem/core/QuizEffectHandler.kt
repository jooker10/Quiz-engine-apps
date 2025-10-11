package futur.apps.composeproject1.quizsystem.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import futur.apps.composeproject1.quizsystem.viewmodels.EffectsViewModel
import futur.apps.composeproject1.viewmodels.QuizUiEffect
import futur.apps.composeproject1.viewmodels.QuizViewModel

@Composable
fun QuizEffectHandler(
    quizViewModel: QuizViewModel,
    effectsViewModel: EffectsViewModel,
) {
    val context = LocalContext.current

    // 🔹 Initialize TTS once
    LaunchedEffect(Unit) {
        effectsViewModel.initTTS(context)
    }

    // 🔹 Collect ONE-TIME effects (sounds, TTS, etc.)
    LaunchedEffect(Unit) {
        quizViewModel.quizUiEffect.collectLatest { effect ->
            val uiState = quizViewModel.quizUiState.value

            when (effect) {
                is QuizUiEffect.PlayCorrectSound ->
                    effectsViewModel.playCorrectSound(uiState.enableSounds, context)

                is QuizUiEffect.PlayWrongSound ->
                    effectsViewModel.playWrongSound(uiState.enableSounds, context)

                is QuizUiEffect.PlayTimerTick ->
                    effectsViewModel.playTimerTick(uiState.enableSounds, context)

                is QuizUiEffect.PlayTimerUrgent ->
                    effectsViewModel.playTimerUrgent(uiState.enableSounds, context)

                is QuizUiEffect.StopTimerSounds ->
                    effectsViewModel.stopTimerSounds()

                is QuizUiEffect.SpeakTextRes -> {
                    val text = context.getString(effect.resId)
                    effectsViewModel.speak(text, enableTTS = uiState.enableTTS)
                }

                QuizUiEffect.None -> Unit
            }
        }
    }
}
