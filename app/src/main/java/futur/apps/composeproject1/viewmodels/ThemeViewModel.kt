package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quiz.ui.theme.AllPalettes
import futur.apps.composeproject1.quiz.ui.theme.AppPalette
import futur.apps.composeproject1.quiz.ui.theme.BluePalette
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    // ---------------- StateFlows ----------------
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> get() = _isLoaded

    val isDarkTheme: StateFlow<Boolean> = dataStore.isDarkThemeEnabled
        .onEach { _isLoaded.value = true } // بمجرد تحميل القيمة نضع isLoaded = true
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false // default dark theme
        )

    private val _selectedPaletteName: StateFlow<String> = dataStore.selectedPaletteName
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            BluePalette.name // default palette
        )
    val selectedPaletteName: StateFlow<String> get() = _selectedPaletteName

    // ---------------- Actions ----------------
    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.saveDarkThemePreference(enabled)
        }
    }

    fun setPalette(name: String) {
        viewModelScope.launch {
            dataStore.setSelectedPalette(name)
        }
    }

    fun getPalette(): AppPalette {
        return AllPalettes.find { it.name == _selectedPaletteName.value } ?: BluePalette
    }
}
