package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {

    // ---------------------------
    // StateFlows for UI consumption
    // ---------------------------
    val username: StateFlow<String> = dataStore.username.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "User"
    )

    val language: StateFlow<String> = dataStore.langue.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "English"
    )

    val isDarkTheme: StateFlow<Boolean> = dataStore.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    // ---------------------------
    // Functions to update values
    // ---------------------------
    fun changeUserName(newName: String) {
        viewModelScope.launch {
            dataStore.setUserName(newName)
        }
    }

    fun changeLanguage(newLanguage: String) {
        viewModelScope.launch {
            dataStore.setLanguage(newLanguage)
        }
    }

    fun changeTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkTheme(enabled)
            }
        }
}