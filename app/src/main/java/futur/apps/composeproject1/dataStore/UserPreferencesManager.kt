/*
package futur.apps.composeproject1.DataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.CategoryName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyMap


val Context.dataStore by preferencesDataStore("app_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val POINTS_KEY = stringPreferencesKey("points_key")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }
    val langue: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }
    val username: Flow<String> = context.dataStore.data.map { it[USERNAME_KEY] ?: "User" }

    val points: Flow<Map<CategoryName, Int>> =
        context.dataStore.data.map { prefs ->
            prefs[POINTS_KEY]?.let { json ->
                try {
                    Json.decodeFromString<Map<CategoryName, Int>>(json)
                } catch (e: Exception) { emptyMap() }
            } ?: emptyMap()
        }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[USERNAME_KEY] = name }
    }

    suspend fun setPoints(points: Map<CategoryName, Int>) {
        val json = Json.encodeToString(points)
        context.dataStore.edit { it[POINTS_KEY] = json}
        }
}*/

package futur.apps.composeproject1.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property to initialize the Preferences DataStore.
 * All application preferences are stored in a single "app_prefs" file.
 */
val Context.dataStore by preferencesDataStore("app_prefs")

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
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Preference keys
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val CATEGORY_POINTS_KEY = stringPreferencesKey("category_points")
    }

    /**
     * Flow representing the current dark theme preference.
     * Emits true if dark theme is enabled, false otherwise.
     */
    val isDarkThemeEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_THEME_KEY] ?: false }

    /**
     * Flow representing the currently selected app language.
     * Defaults to "English".
     */
    val selectedLanguage: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[LANGUAGE_KEY] ?: "English" }

    /**
     * Flow representing the current username of the user.
     * Defaults to "User".
     */
    val username: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[USERNAME_KEY] ?: "User" }

    /**
     * Flow representing the user's accumulated points per category.
     * Stored internally as JSON for serialization/deserialization.
     */
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

    // ---------------- Save Methods ----------------

    /** Save dark theme preference (true = dark mode enabled). */
    suspend fun saveDarkThemePreference(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_THEME_KEY] = enabled }
    }

    /** Save the selected app language. */
    suspend fun saveLanguagePreference(language: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language }
    }

    /** Save or update the username. */
    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs -> prefs[USERNAME_KEY] = name }
    }

    /** Save or update category points (per category). */
    suspend fun saveCategoryPoints(points: Map<Category, Int>) {
        val json = Json.encodeToString(points)
        context.dataStore.edit { prefs -> prefs[CATEGORY_POINTS_KEY] = json}
        }
}