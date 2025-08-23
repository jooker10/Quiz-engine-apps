package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.UserPreferencesManager
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
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    // ---------------------------
    // StateFlows exposed to UI
    // ---------------------------

    /** Current username displayed in the settings screen */
    val username: StateFlow<String> = preferencesManager.username.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "User"
    )

    /** Current language selected in the settings screen */
    val language: StateFlow<String> = preferencesManager.selectedLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "English"
    )

    /** Whether Dark Theme is enabled or not */
    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkThemeEnabled.stateIn(
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
            preferencesManager.saveUsername(newName)
        }
    }

    /** Update the language in DataStore */
    fun changeLanguage(newLanguage: String) {
        viewModelScope.launch {
            preferencesManager.saveLanguagePreference(newLanguage)
        }
    }

    /** Enable or disable dark theme in DataStore */
    fun changeTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.saveDarkThemePreference(enabled)
            }
        }
}