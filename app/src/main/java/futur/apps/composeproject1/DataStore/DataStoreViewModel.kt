package futur.apps.composeproject1.DataStore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {
    var showNavigationBar by mutableStateOf(true)
        private set
    var showFab by mutableStateOf(true)
        private set

    val isDarkMode: StateFlow<Boolean> = dataStore.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, false
    )
    val langue: StateFlow<String> = dataStore.langue.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, "English"
    )
    val username: StateFlow<String> = dataStore.username.stateIn(
        viewModelScope,
        SharingStarted.Eagerly, "User"
    )

    fun changeTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkTheme(enabled)
        }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch {
            dataStore.setLanguage(lang)
        }
    }

    fun changeUserName(username: String) {
        viewModelScope.launch {
            dataStore.setLanguage(username)
        }
    }

    fun setNavigationBarVisibility(visible: Boolean) {
        showNavigationBar = visible
    }

    fun setFabVisibility(visible: Boolean) {
        showFab = visible
    }
}