package futur.apps.composeproject1.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.Category
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
    }

    // ---------------- Flows ----------------
    val isDarkThemeEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }

    val selectedLanguage: Flow<String> =
        context.dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }

    val username: Flow<String> =
        context.dataStore.data.map { it[USERNAME_KEY] ?: "User" }

    val categoryPoints: Flow<Map<Category, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[CATEGORY_POINTS_KEY]?.let { json ->
                try {
                    Json.decodeFromString<Map<Category, Int>>(json)
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

    suspend fun saveCategoryPoints(points: Map<Category, Int>) {
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
}


/*
package futur.apps.composeproject1.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.viewmodels.StatsUiState
import futur.apps.composeproject1.utils.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

*/
/**
 * Extension property to initialize the Preferences DataStore.
 * All application preferences are stored in a single "app_prefs" file.
 *//*

val Context.dataStore by preferencesDataStore("app_prefs")

*/
/**
 * UserPreferencesManager is a wrapper around Jetpack DataStore
 * to handle all persistent user settings and progress data.
 *
 * Responsibilities:
 * - Manage dark theme preference
 * - Manage selected application language
 * - Manage username
 * - Manage quiz/learning points per category
 *
 * This class is annotated with @Singleton to ensure that a single
 * instance is shared across the entire application.
 *//*

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Preference keys
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val CATEGORY_POINTS_KEY = stringPreferencesKey("category_points")
        private val STATS_KEY = stringPreferencesKey("quiz_stats")

    }

    */
/**
     * Flow representing the current dark theme preference.
     * Emits true if dark theme is enabled, false otherwise.
     *//*

    val isDarkThemeEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_THEME_KEY] ?: false }

    */
/**
     * Flow representing the currently selected app language.
     * Defaults to "English".
     *//*

    val selectedLanguage: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[LANGUAGE_KEY] ?: "English" }

    */
/**
     * Flow representing the current username of the user.
     * Defaults to "User".
     *//*

    val username: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[USERNAME_KEY] ?: "User" }

    */
/**
     * Flow representing the user's accumulated points per category.
     * Stored internally as JSON for serialization/deserialization.
     *//*

    val categoryPoints: Flow<Map<Category, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[CATEGORY_POINTS_KEY]?.let { json ->
                try {
                    Json.decodeFromString<Map<Category, Int>>(json)
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
                    StatsUiState() // fallback if corrupted
                }
            } ?: StatsUiState()
        }

    // ---------------- Save Methods ----------------

    */
/** Save dark theme preference (true = dark mode enabled). *//*

    suspend fun saveDarkThemePreference(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_THEME_KEY] = enabled }
    }

    */
/** Save the selected app language. *//*

    suspend fun saveLanguagePreference(language: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language }
    }

    */
/** Save or update the username. *//*

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs -> prefs[USERNAME_KEY] = name }
    }

    */
/** Save or update category points (per category). *//*

  */
/*  suspend fun saveCategoryPoints(points: Map<Category, Int>) {
        val json = Json.encodeToString(points)
        context.dataStore.edit { prefs -> prefs[CATEGORY_POINTS_KEY] = json}
        }*//*

    suspend fun saveStats(stats: StatsUiState) {
        val json = Json.encodeToString(stats)
        context.dataStore.edit { prefs ->
            prefs[STATS_KEY] = json
        }
    }
    suspend fun resetStats() {
        context.dataStore.edit { prefs ->
            prefs.remove(STATS_KEY)
        }
    }

    suspend fun saveCategoryPoints(points: Map<Category, Int>) {
        val json = Json.encodeToString(points)
        context.dataStore.edit { prefs -> prefs[CATEGORY_POINTS_KEY] = json }
    }

    */
/** Reset all saved category points to zero (empty map). *//*

    suspend fun resetAllCategoryPoints() {
        context.dataStore.edit { prefs ->
            prefs.remove(CATEGORY_POINTS_KEY)
        }
    }


}*/
