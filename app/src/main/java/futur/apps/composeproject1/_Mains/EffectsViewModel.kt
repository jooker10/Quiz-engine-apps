/*
package futur.apps.composeproject1._Mains

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.R
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EffectsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private var textToSpeech : TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(application)
        { status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.ENGLISH)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED ) {
                    Log.e("TTS" , "Language not supported")
                }
            }

        }
    }

    fun setTtsLanguage(locale : Locale) {
        textToSpeech?.language = locale
    }

    fun speak(text : String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"tts1")
    }

    private val mediaPlayerCorrect = MediaPlayer.create(application, R.raw.coins_sound1)
    private val mediaPlayerWrong = MediaPlayer.create(application, R.raw.error_sound1)

    fun playCorrectSound() {
        mediaPlayerCorrect.start()
    }
    fun playWrongSound() {
        mediaPlayerWrong.start()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        mediaPlayerCorrect.release()
        mediaPlayerWrong.release()
    }
}*/

package futur.apps.composeproject1._Mains

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.R
import java.util.Locale
import javax.inject.Inject

/**
 * EffectsViewModel handles all sound-related effects in the app:
 * - Text-to-Speech (TTS) for reading words/sentences aloud
 * - Sound effects for correct and wrong answers
 *
 * It is lifecycle-aware and releases resources properly when cleared.
 */
@HiltViewModel
class EffectsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    // ---------------------------
    // Text-to-Speech (TTS)
    // ---------------------------

    private var textToSpeech: TextToSpeech? = null

    init {
        // Initialize TextToSpeech with English as default language
        textToSpeech = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                }
            }
        }
    }

    /** Set a new locale for the Text-to-Speech engine */
    fun setTtsLanguage(locale: Locale) {
        textToSpeech?.language = locale
    }

    /** Speak the given text aloud */
    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }

    // ---------------------------
    // Sound Effects
    // ---------------------------

    private val mediaPlayerCorrect = MediaPlayer.create(application, R.raw.coins_sound1)
    private val mediaPlayerWrong = MediaPlayer.create(application, R.raw.error_sound1)

    /** Play sound for a correct answer */
    fun playCorrectSound() {
        mediaPlayerCorrect.start()
    }

    /** Play sound for a wrong answer */
    fun playWrongSound() {
        mediaPlayerWrong.start()
    }

    // ---------------------------
    // Cleanup
    // ---------------------------

    /** Release resources when ViewModel is destroyed */
    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        mediaPlayerCorrect.release()
        mediaPlayerWrong.release()
        }
}