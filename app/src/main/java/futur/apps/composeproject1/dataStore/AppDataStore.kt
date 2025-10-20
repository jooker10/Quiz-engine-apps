package futur.apps.composeproject1.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.viewmodels.StatsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to initialize DataStore
val Context.dataStore by preferencesDataStore("app_prefs")

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val CATEGORY_POINTS_KEY = stringPreferencesKey("category_points")
        private val STATS_KEY = stringPreferencesKey("quiz_stats")
        private val SELECTED_PALETTE_KEY = stringPreferencesKey("selected_palette")

        // ---------------- New Settings Keys ----------------
        private val AUTO_NEXT_KEY = booleanPreferencesKey("auto_next_on_timeout")
        private val ENABLE_SOUNDS_KEY = booleanPreferencesKey("enable_sounds")
        private val ENABLE_TTS_KEY = booleanPreferencesKey("enable_tts")
        private val MAX_QUESTIONS_KEY = intPreferencesKey("max_questions_per_quiz")

        // 🆕 Replaces the old boolean with an Enum-based string
        private val QUIZ_MODE_KEY = stringPreferencesKey("quiz_mode")

        // (Legacy key kept only for migration)
        private val USE_USER_QUESTIONS_KEY = booleanPreferencesKey("use_user_questions")
    }

    // ---------------- Flows ----------------
    val isDarkThemeEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }

    val selectedLanguage: Flow<String> =
        context.dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }

    val username: Flow<String> =
        context.dataStore.data.map { it[USERNAME_KEY] ?: "User" }

    val categoryPoints: Flow<Map<BuildInCategory, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[CATEGORY_POINTS_KEY]?.let { json ->
                try {
                    Json.decodeFromString<Map<BuildInCategory, Int>>(json)
                } catch (e: Exception) {
                    emptyMap()
                }
            } ?: emptyMap()
        }

    val statsFlow: Flow<StatsUiState> =
        context.dataStore.data.map { prefs ->
            prefs[STATS_KEY]?.let { json ->
                try {
                    Json.decodeFromString<StatsUiState>(json)
                } catch (e: Exception) {
                    StatsUiState()
                }
            } ?: StatsUiState()
        }

    val selectedPaletteName: Flow<String> =
        context.dataStore.data.map { it[SELECTED_PALETTE_KEY] ?: "Blue" }

    // ---------------- New Settings Flows ----------------
    val autoNext: Flow<Boolean> =
        context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: true }

    val enableSounds: Flow<Boolean> =
        context.dataStore.data.map { it[ENABLE_SOUNDS_KEY] ?: true }

    val enableTTS: Flow<Boolean> =
        context.dataStore.data.map { it[ENABLE_TTS_KEY] ?: true }

    val maxQuestions: Flow<Int> =
        context.dataStore.data.map { it[MAX_QUESTIONS_KEY] ?: 10 }

    // 🆕 Global quiz mode (Option 3)
    val globalQuizMode: Flow<QuizMode> =
        context.dataStore.data.map { prefs ->
            // If new key exists → use it
            val stored = prefs[QUIZ_MODE_KEY]
            if (stored != null) {
                QuizMode.values().find { it.name == stored } ?: QuizMode.BUILT_IN
            } else {
                // Else migrate from old boolean key
                val legacy = prefs[USE_USER_QUESTIONS_KEY] ?: false
                if (legacy) QuizMode.USER_CREATED else QuizMode.BUILT_IN
            }
        }

    // ---------------- Save Methods ----------------
    suspend fun saveDarkThemePreference(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun saveLanguagePreference(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { it[USERNAME_KEY] = name }
    }

    suspend fun saveCategoryPoints(points: Map<BuildInCategory, Int>) {
        val json = Json.encodeToString(points)
        context.dataStore.edit { prefs -> prefs[CATEGORY_POINTS_KEY] = json }
    }

    suspend fun resetAllCategoryPoints() {
        context.dataStore.edit { it.remove(CATEGORY_POINTS_KEY) }
    }

    suspend fun saveStats(stats: StatsUiState) {
        val json = Json.encodeToString(stats)
        context.dataStore.edit { it[STATS_KEY] = json }
    }

    suspend fun resetStats() {
        context.dataStore.edit { it.remove(STATS_KEY) }
    }

    // ---------------- Palette Methods ----------------
    suspend fun setSelectedPalette(name: String) {
        context.dataStore.edit { it[SELECTED_PALETTE_KEY] = name }
    }

    // ---------------- New Settings Save Methods ----------------
    suspend fun setAutoNext(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_NEXT_KEY] = enabled }
    }

    suspend fun setEnableSounds(enabled: Boolean) {
        context.dataStore.edit { it[ENABLE_SOUNDS_KEY] = enabled }
    }

    suspend fun setEnableTTS(enabled: Boolean) {
        context.dataStore.edit { it[ENABLE_TTS_KEY] = enabled }
    }

    suspend fun setMaxQuestions(value: Int) {
        context.dataStore.edit { it[MAX_QUESTIONS_KEY] = value }
    }

    // 🆕 Save the new global quiz mode
    suspend fun setGlobalQuizMode(mode: QuizMode) {
        context.dataStore.edit { prefs ->
            prefs[QUIZ_MODE_KEY] = mode.name
            // Optionally remove legacy key after saving once
            prefs.remove(USE_USER_QUESTIONS_KEY)
        }
    }
}
