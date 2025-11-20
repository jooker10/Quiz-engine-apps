package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.utils.QuizMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ============================================================
 * ⚙️ SettingsViewModel.kt
 *
 * Central ViewModel for app-wide preferences and settings.
 * - Reads from [AppDataStore]
 * - Exposes a unified [SettingsUiState]
 * - Provides safe update methods
 * ============================================================
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val isDarkMode: Boolean = false,
    val language: String = "English",
    val selectedPalette: String = "Blue",
    val autoNext: Boolean = true,
    val soundEnabled: Boolean = true,
    val ttsEnabled: Boolean = true,
    val maxQuestions: Int = 10,
    val globalQuizMode: QuizMode = QuizMode.DEFAULT,
    val reminderEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    // ------------------------------------------------------------
    // 🧠 Combine multiple flows into one unified SettingsUiState
    // ------------------------------------------------------------
    val uiState: StateFlow<SettingsUiState> = combine(
        listOf(
            dataStore.isDarkThemeEnabled,
            dataStore.selectedLanguage,
            dataStore.selectedPaletteName,
            dataStore.autoNext,
            dataStore.enableSounds,
            dataStore.enableTTS,
            dataStore.maxQuestions,
            dataStore.globalQuizMode,
            dataStore.showReminderNotifications
        )
    ) { values ->
        val dark = values[0] as? Boolean ?: false
        val lang = values[1] as? String ?: "English"
        val palette = values[2] as? String ?: "Blue"
        val auto = values[3] as? Boolean ?: true
        val sound = values[4] as? Boolean ?: true
        val tts = values[5] as? Boolean ?: true
        val maxQ = values[6] as? Int ?: 10
        val quizMode = values[7] as? QuizMode ?: QuizMode.DEFAULT
        val reminderEnabled = values[8] as? Boolean ?: true


        SettingsUiState(
            isLoading = false,
            isDarkMode = dark,
            language = lang,
            selectedPalette = palette,
            autoNext = auto,
            soundEnabled = sound,
            ttsEnabled = tts,
            maxQuestions = maxQ,
            globalQuizMode = quizMode,
            reminderEnabled = reminderEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )


    // ------------------------------------------------------------
    // 🔄 Preference Update Functions
    // ------------------------------------------------------------
    fun updateDarkMode(enabled: Boolean) = viewModelScope.launch {
        dataStore.saveDarkThemePreference(enabled)
    }

    fun updateLanguage(language: String) = viewModelScope.launch {
        dataStore.saveLanguagePreference(language)
    }

    fun updatePalette(palette: String) = viewModelScope.launch {
        dataStore.setSelectedPalette(palette)
    }

    fun updateAutoNext(enabled: Boolean) = viewModelScope.launch {
        dataStore.setAutoNext(enabled)
    }

    fun updateSound(enabled: Boolean) = viewModelScope.launch {
        dataStore.setEnableSounds(enabled)
    }

    fun updateTTS(enabled: Boolean) = viewModelScope.launch {
        dataStore.setEnableTTS(enabled)
    }

    fun updateMaxQuestions(value: Int) = viewModelScope.launch {
        dataStore.setMaxQuestions(value)
    }
    fun updateReminderEnabled(enabled: Boolean) = viewModelScope.launch {
        dataStore.setShowReminderNotifications(enabled)
    }


}
