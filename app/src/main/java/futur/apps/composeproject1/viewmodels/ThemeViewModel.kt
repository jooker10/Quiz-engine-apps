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
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {

    //  StateFlow (UI-Friendly)
    val isDarkTheme: StateFlow<Boolean> = dataStore.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false // default value
    )

    //  DataStore
    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkTheme(enabled)
            }
        }
}