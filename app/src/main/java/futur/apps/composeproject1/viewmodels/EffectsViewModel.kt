package futur.apps.composeproject1.viewmodels

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import futur.apps.composeproject1.quizsystem.core.QuizConfig

/**
 * EffectsViewModel
 *
 * Handles:
 * - Text-to-Speech (TTS) for reading questions or messages
 * - Sound effects (correct, wrong, timer tick, timer urgent)
 *
 * Why separate ViewModel?
 * ✅ Keeps sound & TTS logic isolated from quiz logic
 * ✅ Automatically released when ViewModel is destroyed
 * ✅ Buyers can easily extend (add more sounds or change voices)
 */
class EffectsViewModel : ViewModel() {

    // --- Media Players for sound effects ---
    private var tickPlayer: MediaPlayer? = null   // Looping ticking sound (normal countdown)
    private var urgentPlayer: MediaPlayer? = null // Looping urgent sound (last few seconds)

    // --- Text to Speech engine ---
    private var textToSpeech: TextToSpeech? = null

    // Use default config (e.g., language comes from QuizUiState)
    private val quizUiState: QuizUiState = QuizUiState()

    // --------------------------------------------------------------------
    // Text-To-Speech (TTS)
    // --------------------------------------------------------------------

    /**
     * Initialize the TTS engine (call once, e.g., in Activity onCreate).
     */
    fun initTTS(context: Context) {
        if (textToSpeech != null) return // already initialized

        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = quizUiState.ttsLanguage
                Log.d("EffectsViewModel", "TTS initialized successfully")
            } else {
                Log.e("EffectsViewModel", "TTS initialization failed")
            }
        }
    }

    /**
     * Speak the given text if TTS is enabled.
     */
    fun speak(text: String, enableTTS: Boolean) {
        if (!enableTTS || textToSpeech == null) return

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,   // Replace previous speech
            null,
            System.currentTimeMillis().toString()
        )
    }

    // --------------------------------------------------------------------
    // Sound Effects
    // --------------------------------------------------------------------

    /**
     * Play "Correct Answer" sound once.
     */
    fun playCorrectSound(enable: Boolean, context: Context) {
        if (!enable) return
        MediaPlayer.create(context, QuizConfig.SOUND_CORRECT).apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    /**
     * Play "Wrong Answer" sound once.
     */
    fun playWrongSound(enable: Boolean, context: Context) {
        if (!enable) return
        MediaPlayer.create(context, QuizConfig.SOUND_WRONG).apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    /**
     * Play looping ticking sound (normal timer countdown).
     */
    fun playTimerTick(enable: Boolean, context: Context) {
        if (!enable) return
        if (tickPlayer?.isPlaying == true) return

        stopTimerSounds() // Ensure no overlap
        tickPlayer = MediaPlayer.create(context, quizUiState.soundTimerTick).apply {
            isLooping = true
            start()
        }
    }

    /**
     * Play looping urgent ticking sound (last seconds of timer).
     */
    fun playTimerUrgent(enable: Boolean, context: Context) {
        if (!enable) return
        if (urgentPlayer?.isPlaying == true) return

        stopTimerSounds() // Ensure no overlap
        urgentPlayer = MediaPlayer.create(context, quizUiState.soundTimerUrgent).apply {
            isLooping = true
            start()
        }
    }

    /**
     * Stop and release all timer sounds (tick + urgent).
     */
    fun stopTimerSounds() {
        tickPlayer?.stop()
        tickPlayer?.release()
        tickPlayer = null

        urgentPlayer?.stop()
        urgentPlayer?.release()
        urgentPlayer = null
    }

    // --------------------------------------------------------------------
    // Cleanup
    // --------------------------------------------------------------------

    /**
     * Called automatically when ViewModel is destroyed.
     * Release TTS and sounds to prevent memory leaks.
     */
    public override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        stopTimerSounds()
    }
}