package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel provides state and functions for the SettingsScreen.
 * It interacts with DataStoreManager to persist user preferences such as:
 * - Username
 * - Language
 * - Dark Theme toggle
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore : AppDataStore
) : ViewModel() {

    // ---------------------------
    // StateFlows exposed to UI
    // ---------------------------

    /** Current username displayed in the settings screen */
    val username: StateFlow<String> = dataStore.username.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "User"
    )

    /** Current language selected in the settings screen */
    val language: StateFlow<String> = dataStore.selectedLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "English"
    )

    /** Whether Dark Theme is enabled or not */
    val isDarkTheme: StateFlow<Boolean> = dataStore.isDarkThemeEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    // ---------------------------
    // Functions to update preferences
    // ---------------------------

    /** Update the username in DataStore */
    fun changeUserName(newName: String) {
        viewModelScope.launch {
            dataStore.saveUsername(newName)
        }
    }

    /** Update the language in DataStore */
    fun changeLanguage(newLanguage: String) {
        viewModelScope.launch {
            dataStore.saveLanguagePreference(newLanguage)
        }
    }

    /** Enable or disable dark theme in DataStore */
    fun changeTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.saveDarkThemePreference(enabled)
            }
        }
}