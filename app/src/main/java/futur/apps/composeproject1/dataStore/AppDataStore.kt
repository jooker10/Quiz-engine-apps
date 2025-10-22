package futur.apps.composeproject1.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.viewmodels.StatsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("app_prefs")

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ------------------------------------------------------------
    // 🔑 Keys
    // ------------------------------------------------------------
    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val USERNAME_KEY = stringPreferencesKey("username")
        val SELECTED_PALETTE_KEY = stringPreferencesKey("selected_palette")

        // Points & Stats (split by mode)
        val BUILTIN_POINTS_KEY = stringPreferencesKey("builtin_category_points")
        val USER_POINTS_KEY = stringPreferencesKey("user_category_points")
        val BUILTIN_STATS_KEY = stringPreferencesKey("builtin_stats")
        val USER_STATS_KEY = stringPreferencesKey("user_stats")

        // Behavior Settings
        val AUTO_NEXT_KEY = booleanPreferencesKey("auto_next_on_timeout")
        val ENABLE_SOUNDS_KEY = booleanPreferencesKey("enable_sounds")
        val ENABLE_TTS_KEY = booleanPreferencesKey("enable_tts")
        val MAX_QUESTIONS_KEY = intPreferencesKey("max_questions_per_quiz")

        // Global Mode
        val QUIZ_MODE_KEY = stringPreferencesKey("quiz_mode")
        val USE_USER_QUESTIONS_KEY = booleanPreferencesKey("use_user_questions") // legacy
    }

    // ------------------------------------------------------------
    // ⚙️ JSON Setup
    // ------------------------------------------------------------
    private val safeJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // ------------------------------------------------------------
    // 🌙 Theme & UI
    // ------------------------------------------------------------
    val isDarkThemeEnabled = context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }
    val selectedLanguage = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }
    val username = context.dataStore.data.map { it[USERNAME_KEY] ?: "User" }
    val selectedPaletteName = context.dataStore.data.map { it[SELECTED_PALETTE_KEY] ?: "Blue" }

    // ------------------------------------------------------------
    // 🧮 Points per mode
    // ------------------------------------------------------------
    val builtInCategoryPoints: Flow<Map<DefaultCategory, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[BUILTIN_POINTS_KEY]?.let { json ->
                runCatching {
                    safeJson.decodeFromString<Map<DefaultCategory, Int>>(json)
                }.getOrElse { emptyMap() }
            } ?: emptyMap()
        }

    val userCategoryPoints: Flow<Map<String, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[USER_POINTS_KEY]?.let { json ->
                runCatching {
                    safeJson.decodeFromString<Map<String, Int>>(json)
                }.getOrElse { emptyMap() }
            } ?: emptyMap()
        }

    // ------------------------------------------------------------
    // 📊 Stats per mode
    // ------------------------------------------------------------
    val builtInStats: Flow<StatsUiState> =
        context.dataStore.data.map { prefs ->
            prefs[BUILTIN_STATS_KEY]?.let { json ->
                runCatching { safeJson.decodeFromString<StatsUiState>(json) }
                    .getOrElse { StatsUiState() }
            } ?: StatsUiState()
        }

    val userStats: Flow<StatsUiState> =
        context.dataStore.data.map { prefs ->
            prefs[USER_STATS_KEY]?.let { json ->
                runCatching { safeJson.decodeFromString<StatsUiState>(json) }
                    .getOrElse { StatsUiState() }
            } ?: StatsUiState()
        }

    // ------------------------------------------------------------
    // ⚙️ Behavior Settings
    // ------------------------------------------------------------
    val autoNext = context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: true }
    val enableSounds = context.dataStore.data.map { it[ENABLE_SOUNDS_KEY] ?: true }
    val enableTTS = context.dataStore.data.map { it[ENABLE_TTS_KEY] ?: true }
    val maxQuestions = context.dataStore.data.map { it[MAX_QUESTIONS_KEY] ?: 10 }

    // ------------------------------------------------------------
    // 🌍 Global Quiz Mode
    // ------------------------------------------------------------
    val globalQuizMode: Flow<QuizMode> =
        context.dataStore.data.map { prefs ->
            prefs[QUIZ_MODE_KEY]?.let { saved ->
                QuizMode.entries.find { it.name == saved } ?: QuizMode.DEFAULT
            } ?: run {
                val legacy = prefs[USE_USER_QUESTIONS_KEY] ?: false
                if (legacy) QuizMode.CUSTOM else QuizMode.DEFAULT
            }
        }

    // ------------------------------------------------------------
    // 💾 Save / Update Methods
    // ------------------------------------------------------------
    // Theme
    suspend fun saveDarkThemePreference(enabled: Boolean) = context.dataStore.edit { it[DARK_THEME_KEY] = enabled }
    suspend fun saveLanguagePreference(language: String) = context.dataStore.edit { it[LANGUAGE_KEY] = language }
    suspend fun saveUsername(name: String) = context.dataStore.edit { it[USERNAME_KEY] = name }
    suspend fun setSelectedPalette(name: String) = context.dataStore.edit { it[SELECTED_PALETTE_KEY] = name }

    // Points
    suspend fun saveBuiltInCategoryPoints(points: Map<DefaultCategory, Int>) {
        val json = safeJson.encodeToString(points)
        context.dataStore.edit { it[BUILTIN_POINTS_KEY] = json }
    }

    suspend fun saveUserCategoryPoints(points: Map<String, Int>) {
        val json = safeJson.encodeToString(points)
        context.dataStore.edit { it[USER_POINTS_KEY] = json }
    }

    suspend fun resetAllCategoryPoints() {
        context.dataStore.edit {
            it.remove(BUILTIN_POINTS_KEY)
            it.remove(USER_POINTS_KEY)
        }
    }

    // Stats
    suspend fun saveBuiltInStats(stats: StatsUiState) {
        val json = safeJson.encodeToString(stats)
        context.dataStore.edit { it[BUILTIN_STATS_KEY] = json }
    }

    suspend fun saveUserStats(stats: StatsUiState) {
        val json = safeJson.encodeToString(stats)
        context.dataStore.edit { it[USER_STATS_KEY] = json }
    }

    suspend fun resetStats() {
        context.dataStore.edit {
            it.remove(BUILTIN_STATS_KEY)
            it.remove(USER_STATS_KEY)
        }
    }

    // Behavior
    suspend fun setAutoNext(enabled: Boolean) = context.dataStore.edit { it[AUTO_NEXT_KEY] = enabled }
    suspend fun setEnableSounds(enabled: Boolean) = context.dataStore.edit { it[ENABLE_SOUNDS_KEY] = enabled }
    suspend fun setEnableTTS(enabled: Boolean) = context.dataStore.edit { it[ENABLE_TTS_KEY] = enabled }
    suspend fun setMaxQuestions(value: Int) = context.dataStore.edit { it[MAX_QUESTIONS_KEY] = value }

    // Mode
    suspend fun setGlobalQuizMode(mode: QuizMode) = context.dataStore.edit {
        it[QUIZ_MODE_KEY] = mode.name
        it.remove(USE_USER_QUESTIONS_KEY)
    }
}
