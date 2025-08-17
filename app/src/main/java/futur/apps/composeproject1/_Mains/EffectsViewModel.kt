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
}