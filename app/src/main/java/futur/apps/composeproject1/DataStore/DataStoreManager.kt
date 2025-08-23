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
}