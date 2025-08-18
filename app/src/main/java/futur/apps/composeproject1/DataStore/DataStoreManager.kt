package futur.apps.composeproject1.DataStore

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import futur.apps.composeproject1.utils.CategoryName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONStringer
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("app_prefs")
@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val SCORES_KEY = stringPreferencesKey("quiz_scores")
    }
    val isDarkTheme : Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[DARK_THEME_KEY] ?: false }
    val langue : Flow<String> = context.dataStore.data.map { prefs -> prefs[LANGUAGE_KEY] ?: "English" }
    val username : Flow<String> = context.dataStore.data.map { prefs -> prefs[USERNAME_KEY] ?: "User" }

    suspend fun setScores(scores : Map<CategoryName,Int>) {
        val str = scores.entries.joinToString(";") {
            "${it.key.name}:${it.value}"}
            context.dataStore.edit { prefs ->
                prefs[SCORES_KEY] = str
            }
        }

    val scores : Flow<Map<CategoryName,Int>> = context.dataStore.data.map { prefs ->
        prefs[SCORES_KEY]?.split(";")?.mapNotNull { part ->
            val (key,value) = part.split("=").let {
                if(it.size == 2) it[0] to it[1] else null to null
            }
            try {
                key?.let { CategoryName.valueOf(it)}?.let{ cat ->
                    cat to (value?.toIntOrNull() ?: 0)
                }
            }
            catch (e : Exception) {
                null
            }
            }?.toMap() ?: emptyMap()
        }

    suspend fun setDarkTheme(enabled : Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_THEME_KEY] = enabled }
    }
    suspend fun setLanguage(lang : String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = lang }
    }
    suspend fun setUserName(name : String) {
        context.dataStore.edit { prefs -> prefs[USERNAME_KEY] = name }
    }
}
